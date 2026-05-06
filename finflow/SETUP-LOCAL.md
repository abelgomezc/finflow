# FinFlow - Guía de Configuración Local

## Requisitos Previos

### Software Necesario

| Software | Versión | Verificar |
|----------|---------|-----------|
| Java | 21+ | `java -version` |
| Maven | 3.9+ | `mvn -version` |
| PostgreSQL | 14+ | `psql --version` |
| Docker | 20+ | `docker --version` |
| Node.js | 18+ | `node --version` |
| npm | 9+ | `npm --version` |

### Instalación de Java 21 (Ubuntu/Debian)

```bash
# Agregar repositorio
sudo apt update
sudo apt install -y openjdk-21-jdk

# Verificar instalación
java -version
```

### Instalación de PostgreSQL

```bash
sudo apt install -y postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

---

## 1. Configuración de Base de Datos

### Crear bases de datos

```bash
sudo -u postgres psql
```

Ejecutar en psql:

```sql
-- Crear usuario
CREATE USER finflow WITH PASSWORD 'finflow123';

-- Crear bases de datos
CREATE DATABASE finflow_accounts OWNER finflow;
CREATE DATABASE finflow_transfers OWNER finflow;
CREATE DATABASE finflow_validation OWNER finflow;
CREATE DATABASE finflow_audit OWNER finflow;
CREATE DATABASE finflow_notifications OWNER finflow;

-- Permisos
GRANT ALL PRIVILEGES ON DATABASE finflow_accounts TO finflow;
GRANT ALL PRIVILEGES ON DATABASE finflow_transfers TO finflow;
GRANT ALL PRIVILEGES ON DATABASE finflow_validation TO finflow;
GRANT ALL PRIVILEGES ON DATABASE finflow_audit TO finflow;
GRANT ALL PRIVILEGES ON DATABASE finflow_notifications TO finflow;

\q
```

O usar el script incluido:

```bash
sudo -u postgres psql -f scripts/setup-databases.sql
```

---

## 2. Configuración de Kafka (Docker)

### Iniciar Zookeeper y Kafka

```bash
cd /home/uservdi/Repositories/finflow

# Iniciar Zookeeper
docker-compose up -d zookeeper

# Esperar 10 segundos, luego iniciar Kafka
sleep 10
docker-compose up -d kafka

# Esperar a que Kafka esté healthy, luego crear topics
sleep 20
docker-compose up kafka-init
```

### Verificar que Kafka está corriendo

```bash
docker-compose ps
# Debe mostrar: finflow-kafka (healthy), finflow-zookeeper (Up)
```

---

## 3. Ajustes Realizados a los Microservicios

### 3.1 Deshabilitar gRPC Auto-configuración

Los microservicios tienen dependencias gRPC que causan conflictos de versiones. Se deshabilitaron las auto-configuraciones:

#### finflow-accounts/src/main/java/.../AccountsApplication.java

```java
@SpringBootApplication(exclude = {
    net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration.class,
    net.devh.boot.grpc.server.autoconfigure.GrpcServerFactoryAutoConfiguration.class,
    net.devh.boot.grpc.server.autoconfigure.GrpcServerSecurityAutoConfiguration.class,
    net.devh.boot.grpc.server.autoconfigure.GrpcServerMetricAutoConfiguration.class,
    net.devh.boot.grpc.server.autoconfigure.GrpcServerTraceAutoConfiguration.class,
    net.devh.boot.grpc.server.autoconfigure.GrpcAdviceAutoConfiguration.class,
    net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration.class,
    net.devh.boot.grpc.client.autoconfigure.GrpcClientHealthAutoConfiguration.class,
    net.devh.boot.grpc.client.autoconfigure.GrpcClientMetricAutoConfiguration.class,
    net.devh.boot.grpc.client.autoconfigure.GrpcClientSecurityAutoConfiguration.class,
    net.devh.boot.grpc.client.autoconfigure.GrpcClientTraceAutoConfiguration.class,
    net.devh.boot.grpc.client.autoconfigure.GrpcDiscoveryClientAutoConfiguration.class
})
```

**Aplicar lo mismo en:**
- `finflow-validation/.../ValidationApplication.java`
- `finflow-transfers/.../TransfersApplication.java` (solo client exclusions)

#### finflow-accounts/.../grpc/AccountGrpcService.java

Comentar la anotación `@GrpcService`:

```java
@Slf4j
// @GrpcService  // Deshabilitado temporalmente - requiere compilar proto files primero
@RequiredArgsConstructor
public class AccountGrpcService {
```

### 3.2 Usar @SuperBuilder para Entidades JPA con Herencia

En `finflow-accounts`, las entidades que extienden `BaseEntity` necesitan `@SuperBuilder`:

#### BaseEntity.java

```java
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
public abstract class BaseEntity {
```

#### Customer.java y Account.java

```java
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder  // Cambiar de @Builder a @SuperBuilder
public class Customer extends BaseEntity {
```

### 3.3 Gateway - Deshabilitar RequestRateLimiter

El `RequestRateLimiter` requiere Redis. En `application.properties` solo incluimos el `DedupeResponseHeader`:

```properties
# finflow-gateway/src/main/resources/application.properties

# Default filters (RequestRateLimiter deshabilitado - requiere Redis)
spring.cloud.gateway.default-filters[0]=DedupeResponseHeader=Access-Control-Allow-Origin Access-Control-Allow-Credentials, RETAIN_FIRST
```

### 3.4 Gateway - Configuración de public-paths

Usar string separado por comas con SpEL para inyección:

#### application.properties

```properties
# finflow-gateway/src/main/resources/application.properties

# Public paths (rutas que no requieren autenticación)
finflow.gateway.security.public-paths=/api/v1/auth/**,/actuator/health,/fallback/**,/api/v1/accounts/**,/api/v1/transfers/**
```

#### JwtAuthenticationFilter.java

```java
@Value("#{'${finflow.gateway.security.public-paths}'.split(',')}")
private List<String> publicPaths;
```

### 3.5 Formato de Configuración: Properties vs YAML

Todos los microservicios usan `application.properties` en lugar de `application.yml`.

**Ejemplo de configuración de rutas en el Gateway:**

```properties
# Ruta para accounts-service
spring.cloud.gateway.routes[0].id=accounts-service
spring.cloud.gateway.routes[0].uri=http://localhost:8081
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/v1/accounts/**
spring.cloud.gateway.routes[0].filters[0].name=CircuitBreaker
spring.cloud.gateway.routes[0].filters[0].args.name=accountsCircuitBreaker
spring.cloud.gateway.routes[0].filters[0].args.fallbackUri=forward:/fallback/accounts
spring.cloud.gateway.routes[0].filters[1].name=AddRequestHeader
spring.cloud.gateway.routes[0].filters[1].args._genkey_0=X-Gateway-Request-Id
spring.cloud.gateway.routes[0].filters[1].args._genkey_1=${random.uuid}
```

**Nota:** Se usa notación de índices `[0]`, `[1]` para listas y objetos anidados en formato properties.

---

## 4. Compilar y Arrancar Servicios

### Orden de arranque recomendado

```bash
cd /home/uservdi/Repositories/finflow
```

### 4.1 finflow-accounts (Puerto 8081)

```bash
cd finflow-accounts
mvn clean compile -DskipTests
nohup mvn spring-boot:run -DskipTests > /tmp/finflow-accounts.log 2>&1 &
```

Verificar:
```bash
curl http://localhost:8081/actuator/health
# {"status":"UP"}
```

### 4.2 finflow-validation (Puerto 8083)

```bash
cd ../finflow-validation
mvn clean compile -DskipTests
nohup mvn spring-boot:run -DskipTests > /tmp/finflow-validation.log 2>&1 &
```

Verificar:
```bash
curl http://localhost:8083/actuator/health
```

### 4.3 finflow-transfers (Puerto 8082)

```bash
cd ../finflow-transfers
mvn clean compile -DskipTests
nohup mvn spring-boot:run -DskipTests > /tmp/finflow-transfers.log 2>&1 &
```

Verificar:
```bash
curl http://localhost:8082/actuator/health
```

### 4.4 finflow-audit (Puerto 8084)

```bash
cd ../finflow-audit
mvn clean compile -DskipTests
nohup mvn spring-boot:run -DskipTests > /tmp/finflow-audit.log 2>&1 &
```

### 4.5 finflow-notifications (Puerto 8085)

```bash
cd ../finflow-notifications
mvn clean compile -DskipTests
nohup mvn spring-boot:run -DskipTests > /tmp/finflow-notifications.log 2>&1 &
```

**Nota:** El health check puede mostrar DOWN porque no hay servidor SMTP configurado. El servicio funciona correctamente.

### 4.6 finflow-gateway (Puerto 8080)

```bash
cd ../finflow-gateway
mvn clean compile -DskipTests
nohup mvn spring-boot:run -DskipTests > /tmp/finflow-gateway.log 2>&1 &
```

Verificar:
```bash
curl http://localhost:8080/actuator/health
```

### 4.7 finflow-ui (Puerto 5173)

```bash
cd ../finflow-ui
npm install
nohup npm run dev > /tmp/finflow-ui.log 2>&1 &
```

---

## 5. Verificación Final

### Script de verificación de todos los servicios

```bash
echo "=== Estado de servicios FinFlow ==="
echo ""
echo "1. finflow-accounts (8081):" && curl -s http://localhost:8081/actuator/health
echo ""
echo "2. finflow-transfers (8082):" && curl -s http://localhost:8082/actuator/health
echo ""
echo "3. finflow-validation (8083):" && curl -s http://localhost:8083/actuator/health
echo ""
echo "4. finflow-audit (8084):" && curl -s http://localhost:8084/actuator/health
echo ""
echo "5. finflow-notifications (8085):" && curl -s http://localhost:8085/actuator/health
echo ""
echo "6. finflow-gateway (8080):" && curl -s http://localhost:8080/actuator/health
echo ""
echo "7. Kafka:" && docker-compose ps | grep kafka
```

### Ver puertos en uso

```bash
ss -tlnp | grep -E "8080|8081|8082|8083|8084|8085|5173|9092"
```

---

## 6. URLs de Acceso

| Servicio | URL | Descripción |
|----------|-----|-------------|
| **Frontend** | http://localhost:5173 | Interfaz React |
| **API Gateway** | http://localhost:8080 | Punto de entrada API |
| **Accounts API** | http://localhost:8081/api/v1/accounts | Servicio de cuentas |
| **Transfers API** | http://localhost:8082/api/v1/transfers | Servicio de transferencias |
| **Validation API** | http://localhost:8083/api/v1/validation | Servicio de validación |
| **Audit API** | http://localhost:8084/api/v1/audit | Servicio de auditoría |

---

## 7. Credenciales y Seguridad

### Sistema de Autenticación

El sistema usa **autenticación real** con:
- Usuarios almacenados en PostgreSQL
- Passwords encriptados con **BCrypt** (12 rounds)
- Tokens **JWT** con expiración de 1 hora
- Bloqueo de cuenta después de 5 intentos fallidos
- Rate limiting (10 req/seg por IP)

### Usuarios de Prueba

| Usuario | Password | Rol | Descripción |
|---------|----------|-----|-------------|
| `admin` | `finflow123` | ADMIN | Administrador del sistema |
| `jperez` | `finflow123` | USER | Juan Pérez (cliente) |
| `mgarcia` | `finflow123` | USER | María García (cliente) |
| `empresa` | `finflow123` | BUSINESS | Empresa Demo S.A. |
| `crodriguez` | `finflow123` | USER | Carlos Rodríguez (cliente) |

### Registro de Nuevos Usuarios

- URL: http://localhost:5173/register
- Los nuevos usuarios se crean con rol **USER**
- Password mínimo: 6 caracteres

### Base de Datos

| Parámetro | Valor |
|-----------|-------|
| Host | localhost |
| Puerto | 5432 |
| Usuario | finflow |
| Contraseña | finflow123 |

### Bases de Datos

| Database | Descripción |
|----------|-------------|
| `finflow_accounts` | Cuentas, clientes, **usuarios** |
| `finflow_transfers` | Transferencias |
| `finflow_validation` | Validaciones y reglas |
| `finflow_audit` | Eventos de auditoría |
| `finflow_notifications` | Notificaciones |

### Cuentas Bancarias de Prueba

| Número Cuenta | Cliente | Saldo | Tipo |
|---------------|---------|-------|------|
| 1000000001 | Juan Pérez | $5,000 | Checking |
| 1000000002 | Juan Pérez | $15,000 | Savings |
| 1000000003 | María García | $8,500 | Checking |
| 1000000004 | Empresa Demo | $50,000 | Business |
| 1000000005 | Carlos Rodríguez | $3,000 | Checking |

### Variables de Entorno (Producción)

En producción, configurar estas variables:

```bash
export DATABASE_PASSWORD=<password-seguro>
export JWT_SECRET=<secreto-256-bits-minimo>
export JWT_EXPIRATION=3600000
```

---

## 8. Detener Servicios

### Detener todos los procesos Java

```bash
pkill -f "spring-boot:run"
```

### Detener UI

```bash
pkill -f "vite"
```

### Detener Kafka

```bash
cd /home/uservdi/Repositories/finflow
docker-compose down
```

---

## 9. Logs

Los logs se guardan en `/tmp/`:

```bash
# Ver logs en tiempo real
tail -f /tmp/finflow-accounts.log
tail -f /tmp/finflow-transfers.log
tail -f /tmp/finflow-validation.log
tail -f /tmp/finflow-audit.log
tail -f /tmp/finflow-notifications.log
tail -f /tmp/finflow-gateway.log
tail -f /tmp/finflow-ui.log
```

---

## 10. Troubleshooting

### Error: Puerto ya en uso

```bash
# Encontrar proceso usando el puerto (ej: 8081)
lsof -i :8081
# Matar proceso
kill -9 <PID>
```

### Error: Flyway migration failed

```bash
# Limpiar y re-ejecutar migraciones
sudo -u postgres psql -c "DROP DATABASE finflow_accounts;"
sudo -u postgres psql -c "CREATE DATABASE finflow_accounts OWNER finflow;"
```

### Error: Kafka connection refused

```bash
# Verificar que Kafka esté corriendo
docker-compose ps
# Reiniciar Kafka
docker-compose restart kafka
```

### Error: gRPC ClassNotFound

Verificar que las exclusiones de auto-configuración estén en `@SpringBootApplication`.

---

## Script Completo de Inicio

Crear `start-all.sh`:

```bash
#!/bin/bash

echo "=== Iniciando FinFlow ==="

cd /home/uservdi/Repositories/finflow

# 1. Kafka
echo "Iniciando Kafka..."
docker-compose up -d zookeeper
sleep 10
docker-compose up -d kafka
sleep 20

# 2. Backend services
for service in finflow-accounts finflow-validation finflow-transfers finflow-audit finflow-notifications finflow-gateway; do
    echo "Compilando e iniciando $service..."
    cd $service
    mvn clean compile -DskipTests -q
    nohup mvn spring-boot:run -DskipTests > /tmp/$service.log 2>&1 &
    cd ..
    sleep 5
done

# 3. Frontend
echo "Iniciando frontend..."
cd finflow-ui
npm install
nohup npm run dev > /tmp/finflow-ui.log 2>&1 &

echo ""
echo "=== FinFlow iniciado ==="
echo "Frontend: http://localhost:5173"
echo "API Gateway: http://localhost:8080"
```

Hacer ejecutable:
```bash
chmod +x start-all.sh
./start-all.sh
```

---

## 11. Arquitectura de Seguridad

### Autenticación JWT

```
┌─────────────┐     ┌─────────────┐     ┌─────────────────┐
│   Frontend  │────>│   Gateway   │────>│ Accounts Service│
│  (React)    │     │  (JWT Auth) │     │  (BCrypt + DB)  │
└─────────────┘     └─────────────┘     └─────────────────┘
     │                    │                      │
     │ 1. Login request   │ 2. Validate creds   │
     │──────────────────->│─────────────────────>│
     │                    │                      │
     │                    │ 3. User data         │
     │                    │<─────────────────────│
     │                    │                      │
     │ 4. JWT Token       │                      │
     │<───────────────────│                      │
```

### Componentes de Seguridad

| Componente | Archivo | Función |
|------------|---------|---------|
| **JwtAuthenticationFilter** | `finflow-gateway/.../filter/` | Valida JWT en cada request |
| **RateLimitingFilter** | `finflow-gateway/.../filter/` | Limita requests por IP |
| **AuthService** | `finflow-accounts/.../service/` | Valida credenciales con BCrypt |
| **User Entity** | `finflow-accounts/.../entity/User.java` | Almacena usuarios con password hash |

### Endpoints de Autenticación

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/v1/auth/login` | POST | Login con username/password |
| `/api/v1/auth/register` | POST | Registro de nuevo usuario (rol USER) |
| `/api/v1/auth/validate` | GET | Validar token JWT |
| `/api/v1/auth/refresh` | POST | Renovar token JWT |

### Endpoints Protegidos

Todos los endpoints excepto `/api/v1/auth/**` requieren JWT válido:

```bash
# Sin token - Error 401
curl http://localhost:8080/api/v1/accounts

# Con token - OK
curl http://localhost:8080/api/v1/accounts \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

### Tabla de Usuarios

```sql
-- Ver usuarios en la base de datos
SELECT id, username, email, full_name, role,
       is_active, is_locked, failed_attempts,
       last_login_at
FROM users;
```

# FinFlow - Guía de Configuración en Windows 11

## Requisitos Previos

| Software | Verificar en CMD/PowerShell |
|----------|----------------------------|
| Java 21+ | `java -version` |
| Maven 3.9+ | `mvn -version` |
| Node.js 18+ | `node --version` |
| npm 9+ | `npm --version` |
| Docker Desktop | `docker --version` |
| PostgreSQL 14+ | `psql --version` |

---

## 1. Configuración de Base de Datos (PostgreSQL)

### Opción A: Usando pgAdmin (GUI)

1. Abrir **pgAdmin 4**
2. Click derecho en "Login/Group Roles" → Create → Login/Group Role
   - Name: `finflow`
   - Password: `finflow123`
   - Privileges: Can login = Yes
3. Click derecho en "Databases" → Create → Database (repetir para cada una):

| Database | Owner |
|----------|-------|
| finflow_accounts | finflow |
| finflow_transfers | finflow |
| finflow_validation | finflow |
| finflow_audit | finflow |
| finflow_notifications | finflow |

### Opción B: Usando psql (Terminal)

Abrir **SQL Shell (psql)** o PowerShell:

```powershell
# Conectar como postgres
psql -U postgres
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

---

## 2. Levantar Kafka y Zookeeper (Docker Desktop)

### Paso 1: Asegurarse que Docker Desktop esté corriendo

- Abrir **Docker Desktop** desde el menú inicio
- Esperar a que diga "Docker Desktop is running"

### Paso 2: Abrir PowerShell/CMD en la carpeta finflow

```powershell
cd C:\ruta\a\tu\carpeta\finflow
```

### Paso 3: Levantar Zookeeper primero

```powershell
docker-compose up -d zookeeper
```

Esperar 10-15 segundos...

### Paso 4: Levantar Kafka

```powershell
docker-compose up -d kafka
```

Esperar 20-30 segundos para que Kafka esté "healthy"...

### Paso 5: Crear los topics de Kafka

```powershell
docker-compose up kafka-init
```

### Paso 6: Verificar que estén corriendo

```powershell
docker-compose ps
```

Debes ver algo así:
```
Name                  State       Ports
finflow-zookeeper     Up          0.0.0.0:2181->2181/tcp
finflow-kafka         Up(healthy) 0.0.0.0:9092->9092/tcp
```

### Comando único (alternativa)

Si prefieres un solo comando que haga todo:

```powershell
docker-compose up -d zookeeper && timeout /t 15 && docker-compose up -d kafka && timeout /t 25 && docker-compose up kafka-init
```

---

## 3. Levantar Microservicios en IntelliJ IDEA

### Orden recomendado de arranque

```
1. finflow-accounts     (Puerto 8081) ← PRIMERO
2. finflow-validation   (Puerto 8083)
3. finflow-transfers    (Puerto 8082)
4. finflow-audit        (Puerto 8084)
5. finflow-notifications (Puerto 8085) ← Opcional, tiene error de compilación
6. finflow-gateway      (Puerto 8080) ← ÚLTIMO
```

### Para cada microservicio:

#### Paso 1: Abrir el proyecto en IntelliJ

1. File → Open
2. Seleccionar la carpeta del microservicio (ej: `finflow-accounts`)
3. Confiar en el proyecto si pregunta

#### Paso 2: Esperar que Maven descargue dependencias

- IntelliJ mostrará "Indexing..." y "Downloading..."
- Esperar a que termine (puede tardar unos minutos la primera vez)

#### Paso 3: Ejecutar la aplicación

**Método 1: Desde la clase principal**

1. Navegar a `src/main/java/ec/com/finflow/[servicio]/[Servicio]Application.java`
2. Click derecho → Run '[Servicio]Application'

**Método 2: Desde Maven (panel lateral)**

1. Abrir panel Maven (lado derecho)
2. Expandir: Plugins → spring-boot → spring-boot:run
3. Doble click en `spring-boot:run`

**Método 3: Desde Terminal de IntelliJ**

```powershell
mvn spring-boot:run -DskipTests
```

### Clases principales de cada servicio

| Servicio | Clase Principal |
|----------|-----------------|
| finflow-accounts | `AccountsApplication.java` |
| finflow-validation | `ValidationApplication.java` |
| finflow-transfers | `TransfersApplication.java` |
| finflow-audit | `AuditApplication.java` |
| finflow-notifications | `NotificationsApplication.java` |
| finflow-gateway | `GatewayApplication.java` |

### Verificar que cada servicio esté corriendo

Abrir navegador y visitar:

| Servicio | Health Check URL |
|----------|-----------------|
| accounts | http://localhost:8081/actuator/health |
| validation | http://localhost:8083/actuator/health |
| transfers | http://localhost:8082/actuator/health |
| audit | http://localhost:8084/actuator/health |
| gateway | http://localhost:8080/actuator/health |

Debe mostrar: `{"status":"UP"}`

---

## 4. Levantar Frontend en VSCode

### Paso 1: Abrir la carpeta finflow-ui en VSCode

1. Abrir VSCode
2. File → Open Folder
3. Seleccionar `finflow-ui`

### Paso 2: Abrir terminal en VSCode

- Menú: Terminal → New Terminal
- O presionar: `` Ctrl + ` ``

### Paso 3: Instalar dependencias (solo la primera vez)

```powershell
npm install
```

### Paso 4: Ejecutar el servidor de desarrollo

```powershell
npm run dev
```

### Paso 5: Abrir en navegador

El terminal mostrará algo como:
```
  VITE v5.x.x  ready in xxx ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: http://192.168.x.x:5173/
```

Abrir **http://localhost:5173** en el navegador.

---

## 5. Resumen - Orden de Ejecución

```
┌─────────────────────────────────────────────────────────────┐
│                    ORDEN DE EJECUCIÓN                        │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  PASO 1: Docker Desktop (Kafka)                              │
│  ─────────────────────────────                               │
│  PowerShell en carpeta finflow:                              │
│  > docker-compose up -d zookeeper                            │
│  > (esperar 15 seg)                                          │
│  > docker-compose up -d kafka                                │
│  > (esperar 25 seg)                                          │
│  > docker-compose up kafka-init                              │
│                                                              │
│  PASO 2: IntelliJ - Microservicios (en orden)                │
│  ────────────────────────────────────────────                │
│  1. Abrir finflow-accounts    → Run → esperar UP             │
│  2. Abrir finflow-validation  → Run → esperar UP             │
│  3. Abrir finflow-transfers   → Run → esperar UP             │
│  4. Abrir finflow-audit       → Run → esperar UP             │
│  5. Abrir finflow-gateway     → Run → esperar UP             │
│                                                              │
│  PASO 3: VSCode - Frontend                                   │
│  ─────────────────────────────                               │
│  Terminal en finflow-ui:                                     │
│  > npm install                                               │
│  > npm run dev                                               │
│                                                              │
│  PASO 4: Abrir navegador                                     │
│  ─────────────────────────────                               │
│  http://localhost:5173                                       │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 6. Credenciales de Prueba

### Login en la aplicación

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| admin | finflow123 | ADMIN |
| jperez | finflow123 | USER |
| mgarcia | finflow123 | USER |
| empresa | finflow123 | BUSINESS |

### Base de datos

| Parámetro | Valor |
|-----------|-------|
| Host | localhost |
| Puerto | 5432 |
| Usuario | finflow |
| Contraseña | finflow123 |

---

## 7. Detener Todo

### Detener Frontend (VSCode)

En la terminal de VSCode: `Ctrl + C`

### Detener Microservicios (IntelliJ)

- Click en el botón rojo "Stop" en cada proyecto
- O cerrar IntelliJ

### Detener Kafka/Zookeeper (Docker)

```powershell
cd C:\ruta\a\finflow
docker-compose down
```

---

## 8. Troubleshooting Windows

### Error: Puerto ya en uso

```powershell
# Ver qué usa el puerto (ej: 8081)
netstat -ano | findstr :8081

# Matar proceso por PID
taskkill /PID <numero_pid> /F
```

### Error: Docker no conecta

1. Verificar que Docker Desktop esté corriendo
2. Reiniciar Docker Desktop
3. En Settings → General → verificar "Use WSL 2 based engine"

### Error: Maven no encontrado

Agregar Maven al PATH:
1. Buscar "Variables de entorno" en Windows
2. Editar "Path" del sistema
3. Agregar: `C:\ruta\a\maven\bin`

### Error: Java no encontrado

Agregar JAVA_HOME:
1. Variables de entorno
2. Nueva variable de sistema: `JAVA_HOME` = `C:\Program Files\Java\jdk-21`
3. Agregar a Path: `%JAVA_HOME%\bin`

### Error: Kafka connection refused

Verificar en `docker-compose.yml` que `KAFKA_ADVERTISED_LISTENERS` use `localhost`:
```yaml
KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
```

---

## 9. URLs de Acceso

| Servicio | URL |
|----------|-----|
| **Frontend** | http://localhost:5173 |
| **API Gateway** | http://localhost:8080 |
| **Accounts API** | http://localhost:8081/api/v1/accounts |
| **Transfers API** | http://localhost:8082/api/v1/transfers |

---

## 10. Arquitectura Final

```
┌─────────────────────────────────────────────────────────────┐
│                    Windows 11                                │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────── Docker Desktop ────────────┐                 │
│  │  finflow-zookeeper (2181)              │                 │
│  │  finflow-kafka     (9092)              │                 │
│  └────────────────────────────────────────┘                 │
│                                                              │
│  ┌──────────── IntelliJ IDEA ─────────────────────────┐     │
│  │  finflow-accounts     (8081)                       │     │
│  │  finflow-validation   (8083)                       │     │
│  │  finflow-transfers    (8082)                       │     │
│  │  finflow-audit        (8084)                       │     │
│  │  finflow-gateway      (8080)                       │     │
│  └────────────────────────────────────────────────────┘     │
│                                                              │
│  ┌──────────── VSCode ────────────────────┐                 │
│  │  finflow-ui           (5173)           │                 │
│  └────────────────────────────────────────┘                 │
│                                                              │
│  ┌──────────── PostgreSQL (Local) ────────┐                 │
│  │  5 databases          (5432)           │                 │
│  └────────────────────────────────────────┘                 │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

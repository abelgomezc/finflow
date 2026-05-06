# FinFlow - Guía de Instalación para Ubuntu

Esta guía asume que tienes **Java 21** y **Maven** instalados. Instalaremos PostgreSQL 16, Node.js 20, Apache Kafka 3.7 y el compilador de Protocol Buffers.

---

## Prerrequisitos - Verificar Java y Maven

```bash
java --version    # Debe mostrar OpenJDK 21.x
mvn --version     # Debe mostrar Apache Maven 3.9+
```

Si no tienes Java 21:
```bash
sudo apt install -y openjdk-21-jdk
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc
```

---

## 1. PostgreSQL 16

### 1.1 Agregar repositorio oficial e instalar

```bash
# Instalar dependencias
sudo apt install -y curl ca-certificates gnupg lsb-release

# Agregar clave GPG oficial de PostgreSQL
sudo install -d /usr/share/postgresql-common/pgdg
sudo curl -o /usr/share/postgresql-common/pgdg/apt.postgresql.org.asc --fail https://www.postgresql.org/media/keys/ACCC4CF8.asc

# Agregar repositorio
sudo sh -c 'echo "deb [signed-by=/usr/share/postgresql-common/pgdg/apt.postgresql.org.asc] https://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'

# Actualizar e instalar
sudo apt update
sudo apt install -y postgresql-16 postgresql-contrib-16
```

### 1.2 Iniciar y habilitar el servicio

```bash
sudo systemctl start postgresql
sudo systemctl enable postgresql
sudo systemctl status postgresql
```

### 1.3 Verificar instalación

```bash
psql --version
# Salida esperada: psql (PostgreSQL) 16.x
```

### 1.4 Crear usuario y bases de datos de FinFlow

```bash
# Conectar como usuario postgres
sudo -u postgres psql
```

Dentro de la consola `psql`, ejecutar:

```sql
-- Crear usuario de la aplicación
CREATE USER finflow WITH PASSWORD 'finflow123' CREATEDB;

-- Crear las 5 bases de datos
CREATE DATABASE finflow_accounts OWNER finflow;
CREATE DATABASE finflow_transfers OWNER finflow;
CREATE DATABASE finflow_validation OWNER finflow;
CREATE DATABASE finflow_audit OWNER finflow;
CREATE DATABASE finflow_notifications OWNER finflow;

-- Otorgar privilegios completos
GRANT ALL PRIVILEGES ON DATABASE finflow_accounts TO finflow;
GRANT ALL PRIVILEGES ON DATABASE finflow_transfers TO finflow;
GRANT ALL PRIVILEGES ON DATABASE finflow_validation TO finflow;
GRANT ALL PRIVILEGES ON DATABASE finflow_audit TO finflow;
GRANT ALL PRIVILEGES ON DATABASE finflow_notifications TO finflow;

-- Salir de psql
\q
```

### 1.5 Habilitar extensión uuid-ossp en cada base de datos

```bash
# Ejecutar para cada base de datos
sudo -u postgres psql -d finflow_accounts -c "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";"
sudo -u postgres psql -d finflow_transfers -c "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";"
sudo -u postgres psql -d finflow_validation -c "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";"
sudo -u postgres psql -d finflow_audit -c "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";"
sudo -u postgres psql -d finflow_notifications -c "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";"
```

### 1.6 Configurar autenticación (pg_hba.conf)

```bash
# Editar archivo de autenticación
sudo nano /etc/postgresql/16/main/pg_hba.conf
```

Buscar la línea que dice `local all all peer` y cambiarla a:
```
local   all             all                                     md5
```

También agregar/modificar para conexiones locales IPv4:
```
host    all             all             127.0.0.1/32            md5
host    all             all             ::1/128                 md5
```

Reiniciar PostgreSQL:
```bash
sudo systemctl restart postgresql
```

### 1.7 Verificar conexión con usuario finflow

```bash
psql -U finflow -d finflow_accounts -h localhost -c "SELECT current_database(), current_user;"
# Ingresa password: finflow123
# Debe mostrar: finflow_accounts | finflow
```

---

## 2. Node.js 20 LTS (para el frontend React)

### 2.1 Instalar via NodeSource (método oficial)

```bash
# Descargar e instalar script de configuración
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -

# Instalar Node.js
sudo apt install -y nodejs
```

### 2.2 Verificar instalación

```bash
node --version    # Debe mostrar v20.x.x
npm --version     # Debe mostrar 10.x.x
```

### 2.3 Instalar herramientas globales útiles

```bash
# TypeScript compiler (opcional, Vite lo incluye)
npm install -g typescript

# Verificar
tsc --version
```

---

## 3. Apache Kafka 3.7

### 3.1 Descargar e instalar

```bash
# Descargar Kafka (usa mirror de Apache)
cd /tmp
wget https://downloads.apache.org/kafka/3.7.0/kafka_2.13-3.7.0.tgz

# Si el enlace no funciona, usar un mirror alternativo:
# wget https://archive.apache.org/dist/kafka/3.7.0/kafka_2.13-3.7.0.tgz

# Extraer y mover a /opt
tar -xzf kafka_2.13-3.7.0.tgz
sudo mv kafka_2.13-3.7.0 /opt/kafka

# Crear usuario de sistema para Kafka (opcional pero recomendado)
sudo useradd -r -s /bin/false kafka || true
sudo chown -R $USER:$USER /opt/kafka
```

### 3.2 Agregar Kafka al PATH

```bash
echo '' >> ~/.bashrc
echo '# Apache Kafka' >> ~/.bashrc
echo 'export KAFKA_HOME=/opt/kafka' >> ~/.bashrc
echo 'export PATH=$PATH:$KAFKA_HOME/bin' >> ~/.bashrc
source ~/.bashrc
```

### 3.3 Crear directorios de datos

```bash
sudo mkdir -p /var/kafka-logs /var/zookeeper
sudo chown -R $USER:$USER /var/kafka-logs /var/zookeeper
```

### 3.4 Configurar Kafka (opcional - ajustes de rendimiento)

```bash
# Editar configuración de Kafka
nano /opt/kafka/config/server.properties
```

Cambiar la línea `log.dirs` a:
```properties
log.dirs=/var/kafka-logs
```

```bash
# Editar configuración de Zookeeper
nano /opt/kafka/config/zookeeper.properties
```

Cambiar `dataDir` a:
```properties
dataDir=/var/zookeeper
```

### 3.5 Iniciar Zookeeper y Kafka

**Terminal 1 - Zookeeper:**
```bash
/opt/kafka/bin/zookeeper-server-start.sh /opt/kafka/config/zookeeper.properties
```

**Terminal 2 - Kafka (esperar 10 segundos después de iniciar Zookeeper):**
```bash
/opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties
```

### 3.6 Crear topics de FinFlow

**Terminal 3:**
```bash
# Esperar a que Kafka esté completamente iniciado (30 segundos aproximadamente)

# Topics principales de transferencias
kafka-topics.sh --create --topic finflow.transfers.initiated \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1

kafka-topics.sh --create --topic finflow.transfers.completed \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1

kafka-topics.sh --create --topic finflow.transfers.failed \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1

kafka-topics.sh --create --topic finflow.transfers.reversed \
  --bootstrap-server localhost:9092 \
  --partitions 1 \
  --replication-factor 1

# Topics de auditoría y notificaciones
kafka-topics.sh --create --topic finflow.audit.events \
  --bootstrap-server localhost:9092 \
  --partitions 2 \
  --replication-factor 1

kafka-topics.sh --create --topic finflow.notifications \
  --bootstrap-server localhost:9092 \
  --partitions 2 \
  --replication-factor 1

# Dead Letter Queue para mensajes fallidos
kafka-topics.sh --create --topic finflow.transfers.dlq \
  --bootstrap-server localhost:9092 \
  --partitions 1 \
  --replication-factor 1
```

### 3.7 Verificar topics creados

```bash
kafka-topics.sh --list --bootstrap-server localhost:9092
```

Salida esperada:
```
finflow.audit.events
finflow.notifications
finflow.transfers.completed
finflow.transfers.dlq
finflow.transfers.failed
finflow.transfers.initiated
finflow.transfers.reversed
```

### 3.8 Crear scripts de inicio automático (systemd)

**Servicio Zookeeper:**
```bash
sudo nano /etc/systemd/system/zookeeper.service
```

Contenido:
```ini
[Unit]
Description=Apache Zookeeper
Documentation=http://zookeeper.apache.org
Requires=network.target
After=network.target

[Service]
Type=simple
User=YOUR_USERNAME
ExecStart=/opt/kafka/bin/zookeeper-server-start.sh /opt/kafka/config/zookeeper.properties
ExecStop=/opt/kafka/bin/zookeeper-server-stop.sh
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

**Servicio Kafka:**
```bash
sudo nano /etc/systemd/system/kafka.service
```

Contenido:
```ini
[Unit]
Description=Apache Kafka
Documentation=http://kafka.apache.org
Requires=zookeeper.service
After=zookeeper.service

[Service]
Type=simple
User=YOUR_USERNAME
ExecStart=/opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties
ExecStop=/opt/kafka/bin/kafka-server-stop.sh
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

> **IMPORTANTE:** Reemplaza `YOUR_USERNAME` con tu nombre de usuario real.

```bash
# Recargar systemd y habilitar servicios
sudo systemctl daemon-reload
sudo systemctl enable zookeeper kafka
sudo systemctl start zookeeper
sleep 10
sudo systemctl start kafka
```

---

## 4. Protocol Buffers Compiler

```bash
sudo apt install -y protobuf-compiler

# Verificar
protoc --version
# Salida esperada: libprotoc 3.x.x
```

---

## 5. Verificación Final

Ejecuta este script para verificar que todo está instalado correctamente:

```bash
echo "=== Verificación de Instalación FinFlow ==="
echo ""

echo "Java:"
java --version | head -1

echo ""
echo "Maven:"
mvn --version | head -1

echo ""
echo "Node.js:"
node --version

echo ""
echo "npm:"
npm --version

echo ""
echo "PostgreSQL:"
psql --version

echo ""
echo "Protobuf Compiler:"
protoc --version

echo ""
echo "Kafka Topics:"
kafka-topics.sh --list --bootstrap-server localhost:9092 2>/dev/null || echo "Kafka no está corriendo"

echo ""
echo "PostgreSQL - Conexión de prueba:"
PGPASSWORD=finflow123 psql -U finflow -d finflow_accounts -h localhost -c "SELECT 'Conexión exitosa' as status;" 2>/dev/null || echo "Error conectando a PostgreSQL"

echo ""
echo "=== Verificación Completa ==="
```

---

## 6. Estructura del Proyecto

Una vez instaladas las dependencias, la estructura del proyecto será:

```
finflow/
├── SETUP_UBUNTU.md          # Esta guía
├── README.md                 # Documentación principal
├── docker-compose.yml        # Para deployment alternativo
├── proto/                    # Contratos gRPC (.proto files)
│   ├── accounts.proto
│   └── validation.proto
├── scripts/                  # Scripts de utilidad
│   ├── setup-databases.sql
│   ├── create-kafka-topics.sh
│   └── start-all-services.sh
├── finflow-gateway/          # API Gateway (Puerto 8080)
├── finflow-accounts/         # Servicio de cuentas (Puerto 8081, gRPC 9081)
├── finflow-transfers/        # Servicio de transferencias (Puerto 8082, gRPC 9082)
├── finflow-validation/       # Servicio de validación (Puerto 8083, gRPC 9083)
├── finflow-audit/            # Servicio de auditoría (Puerto 8084)
├── finflow-notifications/    # Servicio de notificaciones (Puerto 8085)
└── finflow-ui/               # Frontend React (Puerto 5173)
```

---

## 7. Orden de Inicio de Servicios

1. **PostgreSQL** (debe estar corriendo)
2. **Zookeeper** → esperar 10 segundos → **Kafka**
3. **finflow-accounts** (base para los demás)
4. **finflow-validation** (depende de accounts)
5. **finflow-transfers** (depende de accounts y validation)
6. **finflow-audit** y **finflow-notifications** (consumers de Kafka)
7. **finflow-gateway** (enruta a todos los anteriores)
8. **finflow-ui** (frontend)

---

## 8. Comandos Útiles

### PostgreSQL
```bash
# Ver bases de datos
sudo -u postgres psql -c "\l"

# Conectar a una BD específica
psql -U finflow -d finflow_accounts -h localhost

# Ver tablas de una BD
\dt

# Ejecutar archivo SQL
psql -U finflow -d finflow_accounts -h localhost -f archivo.sql
```

### Kafka
```bash
# Ver mensajes de un topic en tiempo real
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
  --topic finflow.transfers.completed --from-beginning

# Producir mensaje de prueba
kafka-console-producer.sh --bootstrap-server localhost:9092 \
  --topic finflow.transfers.initiated

# Ver detalles de un topic
kafka-topics.sh --describe --topic finflow.transfers.initiated \
  --bootstrap-server localhost:9092

# Ver offsets de un grupo de consumidores
kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
  --group finflow-audit --describe
```

### Spring Boot
```bash
# Compilar un microservicio
cd finflow-accounts
mvn clean compile

# Ejecutar con profile local
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Ejecutar tests
mvn test

# Compilar sin tests
mvn clean package -DskipTests
```

### React/Vite
```bash
cd finflow-ui

# Instalar dependencias
npm install

# Desarrollo
npm run dev

# Build para producción
npm run build

# Preview del build
npm run preview
```

---

## Troubleshooting

### PostgreSQL no acepta conexiones
```bash
# Verificar que está corriendo
sudo systemctl status postgresql

# Ver logs
sudo tail -f /var/log/postgresql/postgresql-16-main.log

# Verificar pg_hba.conf
sudo cat /etc/postgresql/16/main/pg_hba.conf | grep -v "^#" | grep -v "^$"
```

### Kafka no inicia
```bash
# Verificar que Zookeeper está corriendo primero
/opt/kafka/bin/zookeeper-shell.sh localhost:2181 <<< "ls /"

# Ver logs de Kafka
tail -f /opt/kafka/logs/server.log

# Limpiar datos y reiniciar (CUIDADO: borra todo)
rm -rf /var/kafka-logs/* /var/zookeeper/*
```

### Puerto ya en uso
```bash
# Ver qué proceso usa un puerto
sudo lsof -i :8081
sudo lsof -i :9092

# Matar proceso por PID
kill -9 <PID>
```

---

## Siguiente Paso

Una vez completada esta instalación, ejecuta los microservicios en el orden indicado usando:

```bash
cd finflow-accounts && mvn spring-boot:run
```

(En terminales separadas para cada servicio)

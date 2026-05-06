#!/bin/bash
# ============================================================
# FinFlow - Script para Iniciar Todos los Microservicios
# Cada servicio se ejecuta en una terminal separada
# ============================================================

set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROFILE="${1:-local}"

echo "=============================================="
echo "FinFlow - Iniciando Microservicios"
echo "Directorio: $PROJECT_ROOT"
echo "Profile: $PROFILE"
echo "=============================================="

# Función para abrir nueva terminal y ejecutar servicio
start_service() {
    local service_name=$1
    local port=$2
    local grpc_port=${3:-""}

    local service_dir="$PROJECT_ROOT/$service_name"

    if [ ! -d "$service_dir" ]; then
        echo "ADVERTENCIA: $service_dir no existe, saltando..."
        return
    fi

    local title="FinFlow - $service_name"
    if [ -n "$grpc_port" ]; then
        title="$title (HTTP:$port, gRPC:$grpc_port)"
    else
        title="$title (HTTP:$port)"
    fi

    echo "Iniciando $service_name en puerto $port..."

    # Detectar el emulador de terminal disponible
    if command -v gnome-terminal &> /dev/null; then
        gnome-terminal --title="$title" -- bash -c "cd $service_dir && mvn spring-boot:run -Dspring-boot.run.profiles=$PROFILE; exec bash"
    elif command -v xterm &> /dev/null; then
        xterm -T "$title" -e "cd $service_dir && mvn spring-boot:run -Dspring-boot.run.profiles=$PROFILE; bash" &
    elif command -v konsole &> /dev/null; then
        konsole --new-tab -p tabtitle="$title" -e bash -c "cd $service_dir && mvn spring-boot:run -Dspring-boot.run.profiles=$PROFILE; exec bash" &
    else
        echo "No se encontró emulador de terminal. Ejecutar manualmente:"
        echo "  cd $service_dir && mvn spring-boot:run -Dspring-boot.run.profiles=$PROFILE"
    fi

    # Esperar un poco entre servicios para evitar conflictos de puertos
    sleep 3
}

# Verificar que PostgreSQL está corriendo
echo ""
echo "Verificando PostgreSQL..."
if ! pg_isready -h localhost -p 5432 > /dev/null 2>&1; then
    echo "ERROR: PostgreSQL no está corriendo. Ejecuta: sudo systemctl start postgresql"
    exit 1
fi
echo "PostgreSQL: OK"

# Verificar que Kafka está corriendo
echo ""
echo "Verificando Kafka..."
if ! /opt/kafka/bin/kafka-topics.sh --list --bootstrap-server localhost:9092 > /dev/null 2>&1; then
    echo "ERROR: Kafka no está corriendo."
    echo "Ejecuta en terminales separadas:"
    echo "  Terminal 1: /opt/kafka/bin/zookeeper-server-start.sh /opt/kafka/config/zookeeper.properties"
    echo "  Terminal 2: /opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties"
    exit 1
fi
echo "Kafka: OK"

echo ""
echo "=============================================="
echo "Iniciando servicios en orden..."
echo "=============================================="

# Orden de inicio (respetando dependencias)
# 1. Accounts (base de datos principal)
start_service "finflow-accounts" 8081 9081

echo "Esperando 10 segundos para que finflow-accounts inicie..."
sleep 10

# 2. Validation (depende de accounts)
start_service "finflow-validation" 8083 9083

echo "Esperando 5 segundos..."
sleep 5

# 3. Transfers (depende de accounts y validation)
start_service "finflow-transfers" 8082 9082

echo "Esperando 5 segundos..."
sleep 5

# 4. Audit y Notifications (consumers de Kafka, independientes)
start_service "finflow-audit" 8084
start_service "finflow-notifications" 8085

echo "Esperando 5 segundos..."
sleep 5

# 5. Gateway (enruta a todos los demás)
start_service "finflow-gateway" 8080

echo ""
echo "=============================================="
echo "Todos los servicios backend iniciados"
echo "=============================================="
echo ""
echo "Para iniciar el frontend React:"
echo "  cd $PROJECT_ROOT/finflow-ui && npm run dev"
echo ""
echo "URLs disponibles:"
echo "  - API Gateway:    http://localhost:8080"
echo "  - Accounts:       http://localhost:8081"
echo "  - Transfers:      http://localhost:8082"
echo "  - Validation:     http://localhost:8083"
echo "  - Audit:          http://localhost:8084"
echo "  - Notifications:  http://localhost:8085"
echo "  - Frontend:       http://localhost:5173 (después de npm run dev)"
echo ""

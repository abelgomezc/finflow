#!/bin/bash

# ============================================================
# FinFlow - Script de Inicio Completo
# ============================================================

set -e

FINFLOW_HOME="/home/uservdi/Repositories/finflow"
LOG_DIR="/tmp"

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}"
echo "============================================================"
echo "  FinFlow - Sistema de Transferencias Bancarias"
echo "  Iniciando todos los servicios..."
echo "============================================================"
echo -e "${NC}"

cd "$FINFLOW_HOME"

# ============================================================
# 1. Verificar requisitos
# ============================================================
echo -e "${YELLOW}[1/4] Verificando requisitos...${NC}"

if ! command -v java &> /dev/null; then
    echo -e "${RED}Error: Java no está instalado${NC}"
    exit 1
fi

if ! command -v docker &> /dev/null; then
    echo -e "${RED}Error: Docker no está instalado${NC}"
    exit 1
fi

if ! command -v node &> /dev/null; then
    echo -e "${RED}Error: Node.js no está instalado${NC}"
    exit 1
fi

echo "  - Java: $(java -version 2>&1 | head -1)"
echo "  - Docker: $(docker --version)"
echo "  - Node: $(node --version)"

# ============================================================
# 2. Iniciar Kafka
# ============================================================
echo ""
echo -e "${YELLOW}[2/4] Iniciando Kafka...${NC}"

# Verificar si ya está corriendo
if docker ps | grep -q finflow-kafka; then
    echo "  Kafka ya está corriendo"
else
    docker-compose up -d zookeeper
    echo "  Esperando Zookeeper..."
    sleep 10

    docker-compose up -d kafka
    echo "  Esperando Kafka..."
    sleep 20

    # Crear topics
    docker-compose up kafka-init 2>/dev/null || true
    echo "  Topics creados"
fi

# ============================================================
# 3. Iniciar Backend Services
# ============================================================
echo ""
echo -e "${YELLOW}[3/4] Iniciando servicios backend...${NC}"

SERVICES=(
    "finflow-accounts:8081"
    "finflow-validation:8083"
    "finflow-transfers:8082"
    "finflow-audit:8084"
    "finflow-notifications:8085"
    "finflow-gateway:8080"
)

for SERVICE_PORT in "${SERVICES[@]}"; do
    SERVICE="${SERVICE_PORT%%:*}"
    PORT="${SERVICE_PORT##*:}"

    # Verificar si ya está corriendo
    if curl -s "http://localhost:$PORT/actuator/health" > /dev/null 2>&1; then
        echo "  [$SERVICE] Ya corriendo en puerto $PORT"
    else
        echo "  [$SERVICE] Compilando e iniciando..."
        cd "$FINFLOW_HOME/$SERVICE"
        mvn clean compile -DskipTests -q 2>/dev/null
        nohup mvn spring-boot:run -DskipTests > "$LOG_DIR/$SERVICE.log" 2>&1 &

        # Esperar a que arranque
        echo "  [$SERVICE] Esperando inicio en puerto $PORT..."
        for i in {1..30}; do
            if curl -s "http://localhost:$PORT/actuator/health" > /dev/null 2>&1; then
                echo -e "  [$SERVICE] ${GREEN}OK${NC}"
                break
            fi
            sleep 2
        done
    fi
done

cd "$FINFLOW_HOME"

# ============================================================
# 4. Iniciar Frontend
# ============================================================
echo ""
echo -e "${YELLOW}[4/4] Iniciando frontend...${NC}"

if curl -s http://localhost:5173 > /dev/null 2>&1; then
    echo "  Frontend ya corriendo en puerto 5173"
else
    cd "$FINFLOW_HOME/finflow-ui"

    # Instalar dependencias si no existen
    if [ ! -d "node_modules" ]; then
        echo "  Instalando dependencias npm..."
        npm install --silent
    fi

    nohup npm run dev > "$LOG_DIR/finflow-ui.log" 2>&1 &
    echo "  Frontend iniciando..."
    sleep 5
fi

# ============================================================
# Resumen
# ============================================================
echo ""
echo -e "${GREEN}"
echo "============================================================"
echo "  FinFlow - Servicios Iniciados"
echo "============================================================"
echo -e "${NC}"
echo ""
echo "  Servicios:"
echo "  ----------------------------------------------------------"

for SERVICE_PORT in "${SERVICES[@]}"; do
    SERVICE="${SERVICE_PORT%%:*}"
    PORT="${SERVICE_PORT##*:}"

    if curl -s "http://localhost:$PORT/actuator/health" | grep -q "UP"; then
        echo -e "  ${GREEN}[OK]${NC} $SERVICE -> http://localhost:$PORT"
    else
        echo -e "  ${RED}[--]${NC} $SERVICE -> http://localhost:$PORT"
    fi
done

if curl -s http://localhost:5173 > /dev/null 2>&1; then
    echo -e "  ${GREEN}[OK]${NC} finflow-ui -> http://localhost:5173"
else
    echo -e "  ${YELLOW}[..]${NC} finflow-ui -> http://localhost:5173 (iniciando...)"
fi

echo ""
echo "  Docker:"
echo "  ----------------------------------------------------------"
docker-compose ps 2>/dev/null | grep -E "kafka|zookeeper" | awk '{print "  " $0}'

echo ""
echo "============================================================"
echo -e "  ${GREEN}Frontend:${NC}     http://localhost:5173"
echo -e "  ${GREEN}API Gateway:${NC}  http://localhost:8080"
echo "============================================================"
echo ""
echo "  Login: cualquier usuario / password (min 4 chars)"
echo "  Logs:  tail -f /tmp/finflow-*.log"
echo ""

#!/bin/bash

# ============================================================
# FinFlow - Script de Detención
# ============================================================

FINFLOW_HOME="/home/uservdi/Repositories/finflow"

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${YELLOW}"
echo "============================================================"
echo "  FinFlow - Deteniendo todos los servicios"
echo "============================================================"
echo -e "${NC}"

# Detener servicios Java
echo "Deteniendo servicios backend..."
pkill -f "spring-boot:run" 2>/dev/null && echo "  Servicios Java detenidos" || echo "  No hay servicios Java corriendo"

# Detener frontend
echo "Deteniendo frontend..."
pkill -f "vite" 2>/dev/null && echo "  Frontend detenido" || echo "  Frontend no estaba corriendo"

# Preguntar si detener Kafka
echo ""
read -p "Detener Kafka? (s/N): " response
if [[ "$response" =~ ^([sS])$ ]]; then
    echo "Deteniendo Kafka..."
    cd "$FINFLOW_HOME"
    docker-compose down
    echo "  Kafka detenido"
fi

echo ""
echo -e "${GREEN}Todos los servicios han sido detenidos${NC}"
echo ""

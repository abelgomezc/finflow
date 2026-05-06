#!/bin/bash
# ============================================================
# FinFlow - Script de Creación de Topics de Kafka
# Ejecutar después de iniciar Zookeeper y Kafka
# ============================================================

set -e

BOOTSTRAP_SERVER="${KAFKA_BOOTSTRAP_SERVER:-localhost:9092}"
KAFKA_BIN="${KAFKA_HOME:-/opt/kafka}/bin"

echo "=============================================="
echo "FinFlow - Creación de Topics de Kafka"
echo "Bootstrap Server: $BOOTSTRAP_SERVER"
echo "=============================================="

# Función para crear topic si no existe
create_topic() {
    local topic_name=$1
    local partitions=$2
    local replication=${3:-1}

    echo "Creando topic: $topic_name (particiones: $partitions, replicación: $replication)"

    $KAFKA_BIN/kafka-topics.sh --create \
        --topic "$topic_name" \
        --bootstrap-server "$BOOTSTRAP_SERVER" \
        --partitions "$partitions" \
        --replication-factor "$replication" \
        --if-not-exists
}

# Esperar a que Kafka esté disponible
echo ""
echo "Esperando a que Kafka esté disponible..."
max_attempts=30
attempt=0

while ! $KAFKA_BIN/kafka-topics.sh --list --bootstrap-server "$BOOTSTRAP_SERVER" > /dev/null 2>&1; do
    attempt=$((attempt + 1))
    if [ $attempt -ge $max_attempts ]; then
        echo "ERROR: Kafka no está disponible después de $max_attempts intentos"
        exit 1
    fi
    echo "  Intento $attempt/$max_attempts - Kafka no disponible, esperando..."
    sleep 2
done

echo "Kafka disponible!"
echo ""

# Topics de Transferencias (alta carga - más particiones)
echo "--- Topics de Transferencias ---"
create_topic "finflow.transfers.initiated" 3
create_topic "finflow.transfers.completed" 3
create_topic "finflow.transfers.failed" 3
create_topic "finflow.transfers.reversed" 1

# Topics de Auditoría
echo ""
echo "--- Topics de Auditoría ---"
create_topic "finflow.audit.events" 2

# Topics de Notificaciones
echo ""
echo "--- Topics de Notificaciones ---"
create_topic "finflow.notifications" 2

# Dead Letter Queue
echo ""
echo "--- Dead Letter Queues ---"
create_topic "finflow.transfers.dlq" 1
create_topic "finflow.notifications.dlq" 1

# Listar topics creados
echo ""
echo "=============================================="
echo "Topics creados exitosamente:"
echo "=============================================="
$KAFKA_BIN/kafka-topics.sh --list --bootstrap-server "$BOOTSTRAP_SERVER" | grep "finflow"

echo ""
echo "Para ver detalles de un topic:"
echo "  kafka-topics.sh --describe --topic <nombre> --bootstrap-server $BOOTSTRAP_SERVER"
echo ""
echo "Para consumir mensajes de prueba:"
echo "  kafka-console-consumer.sh --bootstrap-server $BOOTSTRAP_SERVER --topic finflow.transfers.initiated --from-beginning"

# FinFlow - Sistema de Transferencias Bancarias

**Autor:** Abel Gomez

Sistema completo de transferencias bancarias basado en microservicios con arquitectura event-driven.

## Arquitectura

```
                                    ┌─────────────────┐
                                    │   finflow-ui    │
                                    │   (React 18)    │
                                    │   :5173/:3000   │
                                    └────────┬────────┘
                                             │
                                    ┌────────▼────────┐
                                    │ finflow-gateway │
                                    │ (Spring Cloud)  │
                                    │     :8080       │
                                    └────────┬────────┘
                    ┌────────────────────────┼────────────────────────┐
                    │                        │                        │
           ┌────────▼────────┐      ┌────────▼────────┐      ┌────────▼────────┐
           │ finflow-accounts│      │finflow-transfers│      │  finflow-audit  │
           │   :8081/:9081   │◄────►│   :8082/:9082   │─────►│     :8084       │
           └─────────────────┘ gRPC └────────┬────────┘      └────────▲────────┘
                    ▲                        │                        │
                    │ gRPC          ┌────────▼────────┐              │
                    │               │    Apache       │              │
                    └───────────────│    Kafka        │──────────────┘
                                    │    :9092        │
                                    └────────┬────────┘
                    ┌────────────────────────┼────────────────────────┐
                    │                        │                        │
           ┌────────▼────────┐      ┌────────▼────────┐      ┌────────▼────────┐
           │finflow-validation│     │finflow-notific- │      │   PostgreSQL    │
           │   :8083/:9083   │      │    ations       │      │     :5432       │
           └─────────────────┘      │     :8085       │      └─────────────────┘
                                    └─────────────────┘
```

## Componentes

| Servicio | Puerto HTTP | Puerto gRPC | Descripción |
|----------|-------------|-------------|-------------|
| finflow-gateway | 8080 | - | API Gateway con JWT y Circuit Breaker |
| finflow-accounts | 8081 | 9081 | Gestión de cuentas y saldos |
| finflow-transfers | 8082 | 9082 | Orquestación de transferencias (Saga) |
| finflow-validation | 8083 | 9083 | Validación y detección de fraude |
| finflow-audit | 8084 | - | Consumer Kafka para auditoría |
| finflow-notifications | 8085 | - | Consumer Kafka para notificaciones |
| finflow-ui | 5173/3000 | - | Frontend React |

## Tecnologías

- **Backend**: Java 21, Spring Boot 3.2.5, Spring Cloud Gateway
- **Database**: PostgreSQL 16 con stored procedures PL/pgSQL
- **Messaging**: Apache Kafka 3.7
- **Communication**: gRPC 1.63 para comunicación síncrona
- **Frontend**: React 18, Vite, TypeScript, Tailwind CSS
- **Patrones**: Saga Pattern, Event Sourcing, CQRS

## Requisitos

- Java 21 (OpenJDK)
- Maven 3.9+
- Node.js 20+
- PostgreSQL 16
- Apache Kafka 3.7+
- Docker & Docker Compose (opcional)

## Instalación Rápida

### Opción 1: Docker Compose

```bash
# Clonar el repositorio
cd finflow

# Iniciar todos los servicios
docker-compose up -d

# Verificar que todos los servicios están corriendo
docker-compose ps
```

### Opción 2: Instalación Manual

Ver [SETUP_UBUNTU.md](./SETUP_UBUNTU.md) para instrucciones detalladas.

```bash
# 1. Configurar bases de datos
psql -U postgres -f scripts/setup-databases.sql

# 2. Crear topics de Kafka
./scripts/create-kafka-topics.sh

# 3. Compilar todos los microservicios
for dir in finflow-*/; do
  if [ -f "$dir/pom.xml" ]; then
    (cd "$dir" && mvn clean package -DskipTests)
  fi
done

# 4. Iniciar servicios
./scripts/start-all-services.sh

# 5. Iniciar frontend
cd finflow-ui && npm install && npm run dev
```

## Uso

### Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "demo", "password": "demo1234"}'
```

### Crear Transferencia

```bash
curl -X POST http://localhost:8080/api/v1/transfers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "sourceAccountId": "uuid-cuenta-origen",
    "targetAccountId": "uuid-cuenta-destino",
    "amount": 1000.00,
    "currency": "USD",
    "description": "Pago de servicios"
  }'
```

### Consultar Transferencia

```bash
curl http://localhost:8080/api/v1/transfers/{transferId} \
  -H "Authorization: Bearer <token>"
```

## Flujo de Transferencia (Saga Pattern)

```
1. PENDING      → Transferencia creada
2. VALIDATING   → Validación anti-fraude en progreso
3. PROCESSING   → Monto bloqueado, ejecutando débito/crédito
4. COMPLETED    → Transferencia exitosa
   └─ FAILED    → Error con compensación ejecutada
   └─ REVERSED  → Transferencia reversada manualmente
```

## Detección de Fraude

El sistema implementa 5 reglas de fraude configurables:

1. **UnusualAmountRule** - Montos inusuales para el usuario
2. **UnusualHourRule** - Transferencias en horarios sospechosos
3. **FirstTimeRecipientRule** - Primer envío a beneficiario
4. **RapidTransfersRule** - Múltiples transferencias rápidas
5. **BlacklistedAccountRule** - Cuentas en lista negra

Score > 70 = Transferencia rechazada

## Topics de Kafka

| Topic | Descripción |
|-------|-------------|
| finflow.transfers.initiated | Transferencia iniciada |
| finflow.transfers.completed | Transferencia completada |
| finflow.transfers.failed | Transferencia fallida |
| finflow.transfers.reversed | Transferencia reversada |
| finflow.accounts.blocked | Monto bloqueado |
| finflow.accounts.debited | Cuenta debitada |
| finflow.accounts.credited | Cuenta acreditada |

## Stored Procedures Principales

| Procedimiento | Servicio | Descripción |
|--------------|----------|-------------|
| sp_create_transfer | transfers | Crea transferencia con referencia única |
| sp_update_transfer_status | transfers | Actualiza estado con historial |
| sp_block_amount | accounts | Bloquea monto en cuenta |
| sp_debit_account | accounts | Ejecuta débito con validaciones |
| sp_credit_account | accounts | Ejecuta crédito |
| sp_reverse_debit | accounts | Reversa un débito |
| sp_save_audit_event | audit | Guarda evento de auditoría |

## Estructura del Proyecto

```
finflow/
├── SETUP_UBUNTU.md           # Guía de instalación
├── README.md                  # Este archivo
├── docker-compose.yml         # Configuración Docker
├── proto/                     # Contratos gRPC
│   ├── common.proto
│   ├── accounts.proto
│   └── validation.proto
├── scripts/                   # Scripts de configuración
│   ├── setup-databases.sql
│   ├── create-kafka-topics.sh
│   └── start-all-services.sh
├── finflow-gateway/          # API Gateway
├── finflow-accounts/         # Servicio de cuentas
├── finflow-transfers/        # Servicio de transferencias
├── finflow-validation/       # Servicio de validación
├── finflow-audit/            # Servicio de auditoría
├── finflow-notifications/    # Servicio de notificaciones
└── finflow-ui/               # Frontend React
```

## Monitoreo

Cada servicio expone endpoints de Actuator:

- `/actuator/health` - Estado del servicio
- `/actuator/metrics` - Métricas
- `/actuator/prometheus` - Métricas para Prometheus

## Desarrollo

### Agregar nueva regla de fraude

```java
@Component
public class MyCustomRule implements FraudRule {
    @Override
    public String getCode() { return "CUSTOM_RULE"; }

    @Override
    public String getName() { return "Mi Regla Custom"; }

    @Override
    public Optional<FraudIndicator> evaluate(
            ValidateTransferRequest request,
            FraudRuleContext context) {
        // Implementar lógica
        return Optional.empty();
    }
}
```




## Autor

**Abel Gomez**


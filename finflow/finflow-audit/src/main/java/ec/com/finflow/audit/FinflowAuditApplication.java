package ec.com.finflow.audit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación principal del servicio de auditoría.
 * Consume eventos de Kafka y los almacena para auditoría.
 */
@SpringBootApplication
public class FinflowAuditApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinflowAuditApplication.class, args);
    }
}

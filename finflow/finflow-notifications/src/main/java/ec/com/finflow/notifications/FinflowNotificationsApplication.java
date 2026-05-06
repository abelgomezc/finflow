package ec.com.finflow.notifications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Aplicación principal del servicio de notificaciones.
 * Consume eventos de Kafka y envía notificaciones por email.
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class FinflowNotificationsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinflowNotificationsApplication.class, args);
    }
}

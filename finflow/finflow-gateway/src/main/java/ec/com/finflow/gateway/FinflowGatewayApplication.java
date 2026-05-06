package ec.com.finflow.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación principal del API Gateway.
 * Punto de entrada único para todos los microservicios de FinFlow.
 */
@SpringBootApplication
public class FinflowGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinflowGatewayApplication.class, args);
    }
}

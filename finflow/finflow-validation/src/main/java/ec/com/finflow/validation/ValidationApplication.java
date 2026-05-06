package ec.com.finflow.validation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Punto de entrada del microservicio FinFlow Validation.
 * Motor de validación y anti-fraude para transferencias.
 *
 * <p>Características principales:
 * <ul>
 *   <li>Validación de cuentas y saldos via gRPC</li>
 *   <li>Motor de reglas de fraude configurable</li>
 *   <li>Cálculo de fraud score basado en múltiples indicadores</li>
 *   <li>Gestión de lista negra de cuentas</li>
 *   <li>Perfiles de usuario para detección de anomalías</li>
 * </ul>
 */
@SpringBootApplication(exclude = {
    net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration.class,
    net.devh.boot.grpc.server.autoconfigure.GrpcServerFactoryAutoConfiguration.class,
    net.devh.boot.grpc.server.autoconfigure.GrpcServerSecurityAutoConfiguration.class,
    net.devh.boot.grpc.server.autoconfigure.GrpcServerMetricAutoConfiguration.class,
    net.devh.boot.grpc.server.autoconfigure.GrpcServerTraceAutoConfiguration.class,
    net.devh.boot.grpc.server.autoconfigure.GrpcAdviceAutoConfiguration.class,
    net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration.class,
    net.devh.boot.grpc.client.autoconfigure.GrpcClientHealthAutoConfiguration.class,
    net.devh.boot.grpc.client.autoconfigure.GrpcClientMetricAutoConfiguration.class,
    net.devh.boot.grpc.client.autoconfigure.GrpcClientSecurityAutoConfiguration.class,
    net.devh.boot.grpc.client.autoconfigure.GrpcClientTraceAutoConfiguration.class,
    net.devh.boot.grpc.client.autoconfigure.GrpcDiscoveryClientAutoConfiguration.class
})
@EnableScheduling
public class ValidationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ValidationApplication.class, args);
    }
}

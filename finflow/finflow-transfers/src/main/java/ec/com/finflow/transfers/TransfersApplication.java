package ec.com.finflow.transfers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Punto de entrada del microservicio FinFlow Transfers.
 * Orquestador central que implementa el patrón Saga.
 *
 * <p>Características principales:
 * <ul>
 *   <li>Orquesta el flujo completo de transferencias</li>
 *   <li>Implementa patrón Saga con compensating transactions</li>
 *   <li>Cliente gRPC para finflow-accounts y finflow-validation</li>
 *   <li>Publicador Kafka para eventos de transferencia</li>
 * </ul>
 */
@SpringBootApplication(exclude = {
    net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration.class,
    net.devh.boot.grpc.client.autoconfigure.GrpcClientHealthAutoConfiguration.class,
    net.devh.boot.grpc.client.autoconfigure.GrpcClientMetricAutoConfiguration.class,
    net.devh.boot.grpc.client.autoconfigure.GrpcClientSecurityAutoConfiguration.class,
    net.devh.boot.grpc.client.autoconfigure.GrpcClientTraceAutoConfiguration.class,
    net.devh.boot.grpc.client.autoconfigure.GrpcDiscoveryClientAutoConfiguration.class
})
@EnableAsync
@EnableScheduling
public class TransfersApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransfersApplication.class, args);
    }
}

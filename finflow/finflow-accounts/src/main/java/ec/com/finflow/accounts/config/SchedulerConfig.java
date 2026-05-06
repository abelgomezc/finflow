package ec.com.finflow.accounts.config;

import ec.com.finflow.accounts.repository.BlockedAmountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tareas programadas para mantenimiento de la base de datos.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerConfig {

    private final BlockedAmountRepository blockedAmountRepository;

    /**
     * Expira bloqueos vencidos cada 5 minutos.
     * Los bloqueos que superan su tiempo de expiración
     * se marcan como EXPIRED automáticamente.
     */
    @Scheduled(fixedRate = 300000) // 5 minutos
    @Transactional
    public void expireBlocks() {
        int expired = blockedAmountRepository.expireBlocks();
        if (expired > 0) {
            log.info("Expired {} blocked amounts", expired);
        }
    }
}

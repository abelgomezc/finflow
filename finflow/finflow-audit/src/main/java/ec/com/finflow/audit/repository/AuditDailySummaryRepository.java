package ec.com.finflow.audit.repository;

import ec.com.finflow.audit.domain.entity.AuditDailySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para resúmenes diarios de auditoría.
 */
@Repository
public interface AuditDailySummaryRepository extends JpaRepository<AuditDailySummary, Long> {

    Optional<AuditDailySummary> findBySummaryDateAndEventCategoryAndEventType(
            LocalDate summaryDate, String eventCategory, String eventType);

    List<AuditDailySummary> findBySummaryDateOrderByEventCategoryAscEventTypeAsc(
            LocalDate summaryDate);

    @Query("SELECT ads FROM AuditDailySummary ads " +
           "WHERE ads.summaryDate BETWEEN :startDate AND :endDate " +
           "ORDER BY ads.summaryDate DESC, ads.eventCategory ASC")
    List<AuditDailySummary> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT ads FROM AuditDailySummary ads " +
           "WHERE ads.eventCategory = :category " +
           "AND ads.summaryDate BETWEEN :startDate AND :endDate " +
           "ORDER BY ads.summaryDate DESC")
    List<AuditDailySummary> findByCategoryAndDateRange(
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(ads.eventCount) FROM AuditDailySummary ads " +
           "WHERE ads.summaryDate = :date")
    Long getTotalEventCountForDate(@Param("date") LocalDate date);

    @Query("SELECT SUM(ads.totalAmount) FROM AuditDailySummary ads " +
           "WHERE ads.eventCategory = 'TRANSFER' " +
           "AND ads.summaryDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal getTotalTransferAmountForDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}

package ec.com.finflow.audit.mapper;

import ec.com.finflow.audit.domain.entity.AuditAlert;
import ec.com.finflow.audit.domain.entity.AuditEvent;
import ec.com.finflow.audit.dto.response.AlertResponse;
import ec.com.finflow.audit.dto.response.AuditEventResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-02T14:04:57-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class AuditMapperImpl implements AuditMapper {

    @Override
    public AuditEventResponse toResponse(AuditEvent auditEvent) {
        if ( auditEvent == null ) {
            return null;
        }

        AuditEventResponse.AuditEventResponseBuilder auditEventResponse = AuditEventResponse.builder();

        auditEventResponse.id( auditEvent.getId() );
        auditEventResponse.correlationId( auditEvent.getCorrelationId() );
        auditEventResponse.eventId( auditEvent.getEventId() );
        auditEventResponse.eventType( auditEvent.getEventType() );
        auditEventResponse.eventCategory( auditEvent.getEventCategory() );
        auditEventResponse.entityType( auditEvent.getEntityType() );
        if ( auditEvent.getEntityId() != null ) {
            auditEventResponse.entityId( auditEvent.getEntityId().toString() );
        }
        Map<String, Object> map = auditEvent.getEventData();
        if ( map != null ) {
            auditEventResponse.eventData( new LinkedHashMap<String, Object>( map ) );
        }
        auditEventResponse.sourceService( auditEvent.getSourceService() );
        auditEventResponse.userId( auditEvent.getUserId() );
        auditEventResponse.eventTimestamp( auditEvent.getEventTimestamp() );
        auditEventResponse.receivedAt( auditEvent.getReceivedAt() );

        return auditEventResponse.build();
    }

    @Override
    public AlertResponse toAlertResponse(AuditAlert alert) {
        if ( alert == null ) {
            return null;
        }

        AlertResponse.AlertResponseBuilder alertResponse = AlertResponse.builder();

        alertResponse.auditEventId( alertAuditEventId( alert ) );
        alertResponse.id( alert.getId() );
        alertResponse.alertType( alert.getAlertType() );
        alertResponse.severity( alert.getSeverity() );
        alertResponse.title( alert.getTitle() );
        alertResponse.description( alert.getDescription() );
        alertResponse.status( alert.getStatus() );
        alertResponse.acknowledgedBy( alert.getAcknowledgedBy() );
        alertResponse.acknowledgedAt( alert.getAcknowledgedAt() );
        alertResponse.resolvedBy( alert.getResolvedBy() );
        alertResponse.resolvedAt( alert.getResolvedAt() );
        alertResponse.resolutionNotes( alert.getResolutionNotes() );
        alertResponse.createdAt( alert.getCreatedAt() );

        return alertResponse.build();
    }

    private Long alertAuditEventId(AuditAlert auditAlert) {
        if ( auditAlert == null ) {
            return null;
        }
        AuditEvent auditEvent = auditAlert.getAuditEvent();
        if ( auditEvent == null ) {
            return null;
        }
        Long id = auditEvent.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}

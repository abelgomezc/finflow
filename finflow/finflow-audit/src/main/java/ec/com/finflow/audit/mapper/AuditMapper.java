package ec.com.finflow.audit.mapper;

import ec.com.finflow.audit.domain.entity.AuditAlert;
import ec.com.finflow.audit.domain.entity.AuditEvent;
import ec.com.finflow.audit.dto.response.AlertResponse;
import ec.com.finflow.audit.dto.response.AuditEventResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Mapper para entidades de auditoría.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuditMapper {

    AuditEventResponse toResponse(AuditEvent auditEvent);

    @Mapping(target = "auditEventId", source = "auditEvent.id")
    AlertResponse toAlertResponse(AuditAlert alert);
}

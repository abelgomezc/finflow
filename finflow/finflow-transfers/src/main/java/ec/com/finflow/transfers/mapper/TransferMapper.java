package ec.com.finflow.transfers.mapper;

import ec.com.finflow.transfers.domain.entity.Transfer;
import ec.com.finflow.transfers.dto.response.TransferResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper para conversión entre Transfer y DTOs.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransferMapper {

    TransferResponse toResponse(Transfer transfer);
}

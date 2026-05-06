package ec.com.finflow.transfers.mapper;

import ec.com.finflow.transfers.domain.entity.Transfer;
import ec.com.finflow.transfers.dto.response.TransferResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-02T14:04:35-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class TransferMapperImpl implements TransferMapper {

    @Override
    public TransferResponse toResponse(Transfer transfer) {
        if ( transfer == null ) {
            return null;
        }

        TransferResponse.TransferResponseBuilder transferResponse = TransferResponse.builder();

        transferResponse.id( transfer.getId() );
        transferResponse.referenceNumber( transfer.getReferenceNumber() );
        transferResponse.sourceAccountId( transfer.getSourceAccountId() );
        transferResponse.sourceAccountNumber( transfer.getSourceAccountNumber() );
        transferResponse.targetAccountId( transfer.getTargetAccountId() );
        transferResponse.targetAccountNumber( transfer.getTargetAccountNumber() );
        transferResponse.amount( transfer.getAmount() );
        transferResponse.currency( transfer.getCurrency() );
        transferResponse.status( transfer.getStatus() );
        transferResponse.description( transfer.getDescription() );
        transferResponse.initiatedBy( transfer.getInitiatedBy() );
        transferResponse.failureReason( transfer.getFailureReason() );
        transferResponse.failureMessage( transfer.getFailureMessage() );
        transferResponse.sourceBalanceAfter( transfer.getSourceBalanceAfter() );
        transferResponse.targetBalanceAfter( transfer.getTargetBalanceAfter() );
        transferResponse.initiatedAt( transfer.getInitiatedAt() );
        transferResponse.completedAt( transfer.getCompletedAt() );
        transferResponse.failedAt( transfer.getFailedAt() );
        transferResponse.correlationId( transfer.getCorrelationId() );

        return transferResponse.build();
    }
}

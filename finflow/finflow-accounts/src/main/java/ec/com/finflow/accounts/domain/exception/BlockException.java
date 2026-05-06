package ec.com.finflow.accounts.domain.exception;

import lombok.Getter;

/**
 * Excepciones relacionadas con operaciones de bloqueo de montos.
 */
@Getter
public class BlockException extends FinFlowException {

    private final Long blockId;

    public BlockException(String message, Long blockId) {
        super(message, "BLOCK_ERROR");
        this.blockId = blockId;
    }

    public BlockException(String message, String errorCode, Long blockId) {
        super(message, errorCode);
        this.blockId = blockId;
    }

    /**
     * Bloqueo no encontrado.
     */
    public static BlockException notFound(Long blockId) {
        return new BlockException(
                "Block not found: " + blockId,
                "BLOCK_NOT_FOUND",
                blockId
        );
    }

    /**
     * Bloqueo no encontrado por referencia.
     */
    public static BlockException notFoundByReference(String reference) {
        return new BlockException(
                "Block not found with reference: " + reference,
                "BLOCK_NOT_FOUND",
                null
        );
    }

    /**
     * Bloqueo no está activo.
     */
    public static BlockException notActive(Long blockId, String currentStatus) {
        return new BlockException(
                "Block is not active. Current status: " + currentStatus,
                "BLOCK_NOT_ACTIVE",
                blockId
        );
    }

    /**
     * Referencia de bloqueo duplicada.
     */
    public static BlockException duplicateReference(String reference) {
        return new BlockException(
                "Block reference already exists: " + reference,
                "DUPLICATE_BLOCK_REFERENCE",
                null
        );
    }

    /**
     * Bloqueo expirado.
     */
    public static BlockException expired(Long blockId) {
        return new BlockException(
                "Block has expired: " + blockId,
                "BLOCK_EXPIRED",
                blockId
        );
    }

    /**
     * Bloqueo ya liberado.
     */
    public static BlockException alreadyReleased(Long blockId) {
        return new BlockException(
                "Block has already been released or executed: " + blockId,
                "BLOCK_ALREADY_RELEASED",
                blockId
        );
    }
}

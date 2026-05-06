package ec.com.finflow.accounts.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class UserActivityResponse {
    private Long id;
    private String activityType;
    private String description;
    private String entityType;
    private String entityId;
    private String ipAddress;
    private OffsetDateTime createdAt;
}

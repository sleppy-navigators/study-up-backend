package sleppynavigators.studyupbackend.domain.common;

import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * Represents the time audit attributes. It tracks the creation and update time of the entity.
 */
@Getter
public abstract class TimeAuditBaseDocument {

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

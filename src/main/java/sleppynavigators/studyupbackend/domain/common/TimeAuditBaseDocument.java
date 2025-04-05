package sleppynavigators.studyupbackend.domain.common;

import java.time.LocalDateTime;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * Represents the time audit attributes. It tracks the creation and update time of the entity.
 */
@Getter
public abstract class TimeAuditBaseDocument {

    @Id
    private ObjectId id;

    // We know that the `ObjectId` contains time information,
    // but we manage the `createdAt` fields ourselves for the performance of the lookup
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

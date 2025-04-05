package sleppynavigators.studyupbackend.domain.common;

import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * Represents the time audit attributes. It tracks the creation and update time of the entity.
 */
@Getter
public abstract class TimeAuditBaseDocument {

    @Id
    private ObjectId id;

    public LocalDateTime getCreatedAt() {
        return LocalDateTime.ofInstant(id.getDate().toInstant(), ZoneId.systemDefault());
    }

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

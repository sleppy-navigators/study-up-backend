package sleppynavigators.studyupbackend.domain.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.util.Optional;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Represents the user and time audit attributes. It tracks the ID of the user who created and updated the entity, and
 * the creation and update time of the entity.
 *
 * @see TimeAuditBaseEntity
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class UserAndTimeAuditBaseEntity extends TimeAuditBaseEntity {

    @CreatedBy
    @Column(updatable = false)
    protected Long createdBy;

    @LastModifiedBy
    @Column
    protected Long updatedBy;

    public Long getLastModifier() {
        return Optional.ofNullable(updatedBy).orElse(createdBy);
    }
}

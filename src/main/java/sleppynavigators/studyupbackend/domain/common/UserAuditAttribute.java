package sleppynavigators.studyupbackend.domain.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Optional;
import org.hibernate.annotations.Immutable;
import sleppynavigators.studyupbackend.infrastructure.common.attribute.listener.UserAuditListener;


/**
 * Represents the user audit attributes. It tracks the ID of the user who created and updated the entity.
 *
 * @see UserAuditListener
 */
@Embeddable
public record UserAuditAttribute(@Immutable @Column(nullable = false) Long createdBy,
                                 @Column Long updatedBy) {

    public Long getLastModifier() {
        return Optional.ofNullable(updatedBy).orElse(createdBy);
    }
}

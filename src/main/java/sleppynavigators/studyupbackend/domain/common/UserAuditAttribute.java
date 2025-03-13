package sleppynavigators.studyupbackend.domain.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Optional;
import org.hibernate.annotations.Immutable;

@Embeddable
public record UserAuditAttribute(@Immutable @Column(nullable = false) Long createdBy,
                                 @Column Long updatedBy) {

    public Long getLastModifier() {
        return Optional.ofNullable(updatedBy).orElse(createdBy);
    }
}

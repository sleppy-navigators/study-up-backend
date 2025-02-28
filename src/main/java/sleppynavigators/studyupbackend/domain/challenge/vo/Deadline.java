package sleppynavigators.studyupbackend.domain.challenge.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
public record Deadline(@Column(nullable = false) LocalDateTime value) {
    public Deadline {
        if (value.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Deadline must not be in the past");
        }
    }

    public boolean isPast() {
        return value.isBefore(LocalDateTime.now());
    }
}

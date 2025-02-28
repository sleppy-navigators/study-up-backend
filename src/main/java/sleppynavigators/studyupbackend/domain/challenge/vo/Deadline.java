package sleppynavigators.studyupbackend.domain.challenge.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
public record Deadline(@Column(nullable = false) LocalDateTime deadline) {

    public Deadline {
        if (deadline.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Deadline must not be in the past");
        }
    }

    public boolean isPast() {
        return deadline.isBefore(LocalDateTime.now());
    }
}

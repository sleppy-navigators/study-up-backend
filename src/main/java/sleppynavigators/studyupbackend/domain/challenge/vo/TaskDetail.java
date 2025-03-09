package sleppynavigators.studyupbackend.domain.challenge.vo;

import jakarta.persistence.Column;
import java.time.LocalDateTime;

public record TaskDetail(@Column(nullable = false) String title,
                         @Column(nullable = false) LocalDateTime deadline) {

    private static final int MAX_LENGTH = 20;

    public TaskDetail {
        validateTitle(title);
        validateDeadline(deadline);
    }

    public boolean isPast() {
        return deadline.isBefore(LocalDateTime.now());
    }

    private void validateTitle(String title) {
        if (title.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Title must not be longer than " + MAX_LENGTH + " characters");
        }
    }

    private void validateDeadline(LocalDateTime deadline) {
        if (deadline.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Deadline must not be in the past");
        }
    }
}

package sleppynavigators.studyupbackend.domain.challenge.vo;

import jakarta.persistence.Column;
import java.time.LocalDateTime;

public record ChallengeDetail(@Column(nullable = false) String title,
                              @Column(nullable = false) LocalDateTime deadline,
                              @Column String description) {

    private static final int MAX_LENGTH = 20;
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    public ChallengeDetail {
        validateTitle(title);
        validateDeadline(deadline);
        validateDescription(description);
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

    private void validateDescription(String description) {
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                    "Description must not be longer than " + MAX_DESCRIPTION_LENGTH + " characters");
        }
    }
}

package sleppynavigators.studyupbackend.domain.challenge.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
public class ChallengeDetail {

    private static final int MAX_LENGTH = 20;
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column
    private String description;

    public ChallengeDetail(String title, String description) {
        this(title, null, description);
    }

    public ChallengeDetail(String title, LocalDateTime deadline, String description) {
        validateTitle(title);
        validateDeadline(deadline);
        validateDescription(description);

        this.title = title;
        this.deadline = deadline;
        this.description = description;
    }

    private void validateTitle(String title) {
        if (title.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Title must not be longer than " + MAX_LENGTH + " characters");
        }
    }

    private void validateDeadline(LocalDateTime deadline) {
        if (deadline != null && deadline.isBefore(LocalDateTime.now())) {
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

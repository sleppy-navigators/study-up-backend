package sleppynavigators.studyupbackend.domain.challenge.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Embeddable
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
public class TaskDetail {

    private static final int MAX_LENGTH = 20;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime deadline;

    public TaskDetail(String title, LocalDateTime deadline) {
        validateTitle(title);
        validateDeadline(deadline);

        this.title = title;
        this.deadline = deadline;
    }

    public boolean isOverdue() {
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

package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import java.time.LocalDateTime;
import sleppynavigators.studyupbackend.domain.challenge.Task;

public record TaskResponse(Long id, String title, LocalDateTime deadline) {

    public static TaskResponse fromEntity(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle().title(),
                task.getDeadline().deadline()
        );
    }
}

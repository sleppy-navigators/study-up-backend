package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import sleppynavigators.studyupbackend.domain.challenge.Task;

public record TaskListResponse(List<TaskListItem> tasks) {

    public record TaskListItem(Long id, String title, LocalDateTime deadline, TaskCertificationDTO certification) {

        public static TaskListItem fromEntity(Task task) {
            return new TaskListItem(
                    task.getId(),
                    task.getTitle().value(),
                    task.getDeadline().value(),
                    TaskCertificationDTO.fromEntity(task.getCertification())
            );
        }
    }

    public static TaskListResponse fromEntities(List<Task> tasks) {
        return new TaskListResponse(
                tasks.stream()
                        .map(TaskListItem::fromEntity)
                        .toList()
        );
    }
}

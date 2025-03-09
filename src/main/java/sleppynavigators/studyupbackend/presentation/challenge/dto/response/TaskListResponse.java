package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.challenge.vo.TaskCertification;

public record TaskListResponse(List<TaskListItem> tasks) {

    public record TaskListItem(Long id, String title, LocalDateTime deadline, TaskCertificationDTO certification) {

        public static TaskListItem fromEntity(Task task) {
            TaskCertification certification = task.getCertification();

            return new TaskListItem(
                    task.getId(),
                    task.getDetail().title(),
                    task.getDetail().deadline(),
                    (certification.isCertified())
                            ? TaskCertificationDTO.fromEntity(task.getCertification())
                            : null
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

package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.challenge.vo.TaskCertification;

public record TaskListResponse(@NotNull @Valid List<TaskListItem> tasks) {

    public record TaskListItem(@NotNull Long id,
                               @NotBlank String title,
                               @NotNull ZonedDateTime deadline,
                               @Valid TaskCertificationDTO certification) {

        public static TaskListItem fromEntity(Task task) {
            TaskCertification certification = task.getCertification();

            return new TaskListItem(
                    task.getId(),
                    task.getDetail().getTitle(),
                    task.getDetail().getDeadline().atZone(ZoneId.systemDefault()),
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

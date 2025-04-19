package sleppynavigators.studyupbackend.presentation.user.dto.response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.challenge.vo.TaskCertification;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskChallengeDTO;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskCertificationDTO;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskGroupDTO;

public record UserTaskListResponse(@NotNull @Valid List<UserTaskListItem> tasks) {

    public record UserTaskListItem(
            @NotNull Long id,
            @NotBlank String title,
            @NotNull ZonedDateTime deadline,
            @Valid TaskCertificationDTO certification,
            @NotNull @Valid TaskChallengeDTO challengeDetail,
            @NotNull @Valid TaskGroupDTO groupDetail) {

        public static UserTaskListItem fromEntity(Task task) {
            TaskCertification taskCertification = task.getCertification();

            return new UserTaskListItem(
                    task.getId(),
                    task.getDetail().getTitle(),
                    task.getDetail().getDeadline().atZone(ZoneId.systemDefault()),
                    (taskCertification.isCertified())
                            ? TaskCertificationDTO.fromEntity(task.getCertification())
                            : null,
                    TaskChallengeDTO.fromEntity(task),
                    TaskGroupDTO.fromEntity(task)
            );
        }
    }

    public static UserTaskListResponse fromEntities(List<Task> tasks) {
        return new UserTaskListResponse(
                tasks.stream()
                        .map(UserTaskListItem::fromEntity)
                        .toList()
        );
    }
}

package sleppynavigators.studyupbackend.presentation.user.dto.response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.challenge.vo.TaskCertification;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskCertificationDTO;

public record UserTaskListResponse(@NotNull @Valid List<UserTaskListItem> tasks) {

    public record UserTaskListItem(
            @NotNull Long id,
            @NotBlank String title,
            @NotNull LocalDateTime deadline,
            @Valid TaskCertificationDTO certification,
            @NotNull @Valid UserTaskChallengeDetail challengeDetail,
            @NotNull @Valid UserTaskGroupDetail groupDetail) {

        public static UserTaskListItem fromEntity(Task task) {
            TaskCertification taskCertification = task.getCertification();

            return new UserTaskListItem(
                    task.getId(),
                    task.getDetail().title(),
                    task.getDetail().deadline(),
                    (taskCertification.isCertified())
                            ? TaskCertificationDTO.fromEntity(task.getCertification())
                            : null,
                    UserTaskChallengeDetail.fromEntity(task),
                    UserTaskGroupDetail.fromEntity(task)
            );
        }
    }

    public record UserTaskChallengeDetail(@NotNull Long challengeId,
                                          @NotBlank String challengeTitle) {

        public static UserTaskChallengeDetail fromEntity(Task task) {
            return new UserTaskChallengeDetail(
                    task.getChallenge().getId(),
                    task.getChallenge().getDetail().title());
        }
    }

    public record UserTaskGroupDetail(@NotNull Long groupId,
                                      @NotBlank String groupName,
                                      @NotNull Boolean currentlyJoined) {

        public static UserTaskGroupDetail fromEntity(Task task) {
            return new UserTaskGroupDetail(
                    task.getChallenge().getGroup().getId(),
                    task.getChallenge().getGroup().getGroupDetail().name(),
                    task.getChallenge().getGroup().hasMember(task.getChallenge().getOwner()));
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

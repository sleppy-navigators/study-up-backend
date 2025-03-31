package sleppynavigators.studyupbackend.presentation.group.dto.response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.challenge.vo.TaskCertification;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskCertificationDTO;

public record GroupTaskListResponse(@NotNull @Valid List<GroupTaskListItem> tasks) {

    public record GroupTaskListItem(@NotNull Long id,
                                    @NotBlank String title,
                                    @NotNull LocalDateTime deadline,
                                    @Valid TaskCertificationDTO certification,
                                    @NotNull @Valid GroupTaskChallengeDetail challenge) {

        public static GroupTaskListItem fromEntity(Task task) {
            TaskCertification taskCertification = task.getCertification();

            return new GroupTaskListItem(
                    task.getId(),
                    task.getDetail().title(),
                    task.getDetail().deadline(),
                    (taskCertification.isCertified()) ?
                            TaskCertificationDTO.fromEntity(taskCertification)
                            : null,
                    GroupTaskChallengeDetail.fromEntity(task));
        }
    }

    public record GroupTaskChallengeDetail(@NotNull Long challengeId,
                                           @NotBlank String challengeTitle,
                                           @NotNull Boolean isCompleted,
                                           @NotNull Boolean currentlyJoined) {

        public static GroupTaskChallengeDetail fromEntity(Task task) {
            return new GroupTaskChallengeDetail(
                    task.getChallenge().getId(),
                    task.getChallenge().getDetail().title(),
                    task.getChallenge().isCompleted(),
                    task.getChallenge().getGroup().hasMember(task.getChallenge().getOwner()));
        }
    }

    public static GroupTaskListResponse fromEntities(List<Task> tasks) {
        return new GroupTaskListResponse(tasks.stream()
                .map(GroupTaskListItem::fromEntity)
                .toList());
    }
}

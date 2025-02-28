package sleppynavigators.studyupbackend.presentation.user.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskCertificationDTO;

public record UserTaskListResponse(List<UserTaskListItem> tasks) {

    public record UserTaskListItem(
            Long id, String title, LocalDateTime deadline, TaskCertificationDTO certification,
            UserTaskChallengeDetail challengeDetail, UserTaskGroupDetail groupDetail) {

        public static UserTaskListItem fromEntity(Task task) {
            return new UserTaskListItem(
                    task.getId(),
                    task.getTitle().value(),
                    task.getDeadline().value(),
                    TaskCertificationDTO.fromEntity(task.getCertification()),
                    UserTaskChallengeDetail.fromEntity(task),
                    UserTaskGroupDetail.fromEntity(task)
            );
        }
    }

    public record UserTaskChallengeDetail(Long challengeId, String challengeTitle) {

        public static UserTaskChallengeDetail fromEntity(Task task) {
            return new UserTaskChallengeDetail(
                    task.getChallenge().getId(),
                    task.getChallenge().getTitle().value());
        }
    }

    public record UserTaskGroupDetail(Long groupId, String groupName) {

        public static UserTaskGroupDetail fromEntity(Task task) {
            return new UserTaskGroupDetail(
                    task.getChallenge().getOwner().getGroup().getId(),
                    task.getChallenge().getOwner().getGroup().getGroupDetail().name());
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

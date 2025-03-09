package sleppynavigators.studyupbackend.presentation.group.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.challenge.vo.TaskCertification;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskCertificationDTO;

public record GroupTaskListResponse(List<GroupTaskListItem> tasks) {

    public record GroupTaskListItem(Long id, String title, LocalDateTime deadline,
                                    TaskCertificationDTO certification, GroupTaskChallengeDetail challenge) {

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

    public record GroupTaskChallengeDetail(Long challengeId, String challengeTitle) {

        public static GroupTaskChallengeDetail fromEntity(Task task) {
            return new GroupTaskChallengeDetail(
                    task.getChallenge().getId(),
                    task.getChallenge().getDetail().title());
        }
    }

    public static GroupTaskListResponse fromEntities(List<Task> tasks) {
        return new GroupTaskListResponse(tasks.stream()
                .map(GroupTaskListItem::fromEntity)
                .toList());
    }
}

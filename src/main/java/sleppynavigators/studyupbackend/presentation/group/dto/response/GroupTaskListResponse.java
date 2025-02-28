package sleppynavigators.studyupbackend.presentation.group.dto.response;

import java.time.LocalDateTime;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskCertificationDTO;

public record GroupTaskListResponse() {

    public record GroupTaskListItem(Long id, String title, LocalDateTime deadline,
                                    TaskCertificationDTO certification, GroupTaskChallengeDetail challenge) {

        public static GroupTaskListItem fromEntity(Task task) {
            return new GroupTaskListItem(
                    task.getId(),
                    task.getTitle().value(),
                    task.getDeadline().value(),
                    TaskCertificationDTO.fromEntity(task.getCertification()),
                    GroupTaskChallengeDetail.fromEntity(task));
        }
    }

    public record GroupTaskChallengeDetail(Long challengeId, String challengeTitle) {

        public static GroupTaskChallengeDetail fromEntity(Task task) {
            return new GroupTaskChallengeDetail(
                    task.getChallenge().getId(),
                    task.getChallenge().getTitle().value());
        }
    }
}

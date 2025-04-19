package sleppynavigators.studyupbackend.presentation.group.dto.response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.challenge.vo.TaskCertification;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.ChallengerDTO;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskCertificationDTO;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskChallengeDTO;

public record GroupTaskListResponse(@NotNull @Valid List<GroupTaskListItem> tasks) {

    public record GroupTaskListItem(@NotNull Long id,
                                    @NotBlank String title,
                                    @NotNull ZonedDateTime deadline,
                                    @NotNull @Valid TaskChallengeDTO challengeDetail,
                                    @NotNull @Valid ChallengerDTO challengerDetail,
                                    @Valid TaskCertificationDTO certification) {

        public static GroupTaskListItem fromEntity(Task task) {
            TaskCertification taskCertification = task.getCertification();

            return new GroupTaskListItem(
                    task.getId(),
                    task.getDetail().getTitle(),
                    task.getDetail().getDeadline().atZone(ZoneId.systemDefault()),
                    TaskChallengeDTO.fromEntity(task),
                    ChallengerDTO.fromEntity(task.getChallenge()),
                    (taskCertification.isCertified()) ?
                            TaskCertificationDTO.fromEntity(taskCertification)
                            : null);
        }
    }

    public static GroupTaskListResponse fromEntities(List<Task> tasks) {
        return new GroupTaskListResponse(tasks.stream()
                .map(GroupTaskListItem::fromEntity)
                .toList());
    }
}

package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import java.time.LocalDateTime;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.challenge.vo.TaskCertification;

public record TaskResponse(Long id, String title, LocalDateTime deadline, TaskCertificationDTO certification) {

    public static TaskResponse fromEntity(Task task) {
        TaskCertification taskCertification = task.getCertification();

        return new TaskResponse(
                task.getId(),
                task.getDetail().title(),
                task.getDetail().deadline(),
                (taskCertification.isCertified())
                        ? TaskCertificationDTO.fromEntity(task.getCertification())
                        : null
        );
    }
}

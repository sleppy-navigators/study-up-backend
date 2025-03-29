package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.challenge.vo.TaskCertification;

public record TaskResponse(@NotNull Long id,
                           @NotBlank String title,
                           @NotNull LocalDateTime deadline,
                           TaskCertificationDTO certification) {

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

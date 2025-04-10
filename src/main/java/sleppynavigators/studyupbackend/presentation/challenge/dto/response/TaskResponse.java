package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.challenge.vo.TaskCertification;

public record TaskResponse(@NotNull Long id,
                           @NotBlank String title,
                           @NotNull ZonedDateTime deadline,
                           @Valid TaskCertificationDTO certification) {

    public static TaskResponse fromEntity(Task task) {
        TaskCertification taskCertification = task.getCertification();

        return new TaskResponse(
                task.getId(),
                task.getDetail().title(),
                task.getDetail().deadline().atZone(ZoneId.systemDefault()),
                (taskCertification.isCertified())
                        ? TaskCertificationDTO.fromEntity(task.getCertification())
                        : null
        );
    }
}

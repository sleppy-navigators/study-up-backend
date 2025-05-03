package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.challenge.vo.TaskCertification;

@Schema(description = "테스크 정보 응답")
public record TaskResponse(
        @Schema(description = "테스크 ID", example = "1")
        @NotNull Long id,

        @Schema(description = "테스크 제목", example = "과제 제출하기")
        @NotBlank String title,

        @Schema(description = "테스크 마감일", example = "2023-10-01T10:00:00Z")
        @NotNull ZonedDateTime deadline,

        @Schema(description = "테스크 인증 정보")
        @Valid TaskCertificationDTO certification) {

    public static TaskResponse fromEntity(Task task) {
        TaskCertification taskCertification = task.getCertification();

        return new TaskResponse(
                task.getId(),
                task.getDetail().getTitle(),
                task.getDetail().getDeadline().atZone(ZoneId.systemDefault()),
                (taskCertification.isCertified())
                        ? TaskCertificationDTO.fromEntity(task.getCertification())
                        : null
        );
    }
}

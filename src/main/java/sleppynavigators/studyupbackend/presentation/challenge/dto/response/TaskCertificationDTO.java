package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import sleppynavigators.studyupbackend.domain.challenge.vo.TaskCertification;

@Schema(description = "테스크 인증")
public record TaskCertificationDTO(
        @Schema(description = "인증 자료 목록 - 외부 링크")
        @NotNull List<URL> externalLinks,

        @Schema(description = "인증 자료 목록 - 이미지")
        @NotNull List<URL> imageUrls,

        @Schema(description = "인증 제출 시간", example = "2023-10-01T10:00:00Z")
        ZonedDateTime certificatedAt) {

    public static TaskCertificationDTO fromEntity(TaskCertification taskCertification) {
        return new TaskCertificationDTO(
                taskCertification.getExternalLinks(),
                taskCertification.getImageUrls(),
                taskCertification.getCertifiedAt().atZone(ZoneId.systemDefault())
        );
    }
}

package sleppynavigators.studyupbackend.presentation.challenge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.net.URL;
import java.util.List;

@Schema(description = "테스크 인증 요청")
public record TaskCertificationRequest(
        @Schema(description = "인증 자료 - 외부 링크", example = "[https://example.com/article]")
        @NotNull List<URL> externalLinks,

        @Schema(description = "인증 자료 - 이미지", example = "[https://example.com/image.jpg]")
        @NotNull List<URL> imageUrls) {
}

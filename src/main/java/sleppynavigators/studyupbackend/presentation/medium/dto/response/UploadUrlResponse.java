package sleppynavigators.studyupbackend.presentation.medium.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.net.URL;

@Schema(description = "업로드 URL 응답")
public record UploadUrlResponse(
        @Schema(description = "업로드 URL", example = "https://example.com/upload")
        @NotNull URL url) {
}

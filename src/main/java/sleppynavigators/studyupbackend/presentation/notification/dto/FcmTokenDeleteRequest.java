package sleppynavigators.studyupbackend.presentation.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "FCM 토큰 삭제 요청")
public record FcmTokenDeleteRequest(
        @Schema(description = "삭제할 디바이스 ID", example = "device_id_example")
        @NotBlank String deviceId
) {
}

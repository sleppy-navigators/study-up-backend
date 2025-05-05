package sleppynavigators.studyupbackend.presentation.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sleppynavigators.studyupbackend.domain.notification.FcmToken;

@Schema(description = "FCM 토큰 요청")
public record FcmTokenRequest(

        @Schema(description = "FCM 토큰", example = "fcm_token_example")
        @NotBlank String token,

        @Schema(description = "디바이스 ID", example = "device_id_example")
        @NotBlank String deviceId,

        @Schema(description = "디바이스 타입 (ANDROID, IOS, WEB)", example = "ANDROID")
        @NotNull FcmToken.DeviceType deviceType
) {

}

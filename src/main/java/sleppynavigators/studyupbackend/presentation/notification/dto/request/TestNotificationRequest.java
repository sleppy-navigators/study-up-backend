package sleppynavigators.studyupbackend.presentation.notification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.net.URL;
import java.util.Map;

@Schema(description = "테스트 알림 전송 요청")
public record TestNotificationRequest(
        @Schema(description = "알림 제목", example = "테스트 알림입니다")
        @NotBlank String title,

        @Schema(description = "알림 내용", example = "이것은 테스트 알림입니다. 정상적으로 수신되었다면 성공입니다.")
        @NotBlank String body,

        @Schema(description = "알림 이미지 URL", example = "https://example.com/image.jpg", nullable = true)
        URL imageUrl,

        @Schema(description = "추가 데이터", nullable = true)
        Map<String, String> data
) {
}

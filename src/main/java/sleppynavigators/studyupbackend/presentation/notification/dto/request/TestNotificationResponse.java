package sleppynavigators.studyupbackend.presentation.notification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "테스트 알림 전송 응답")
public record TestNotificationResponse(

        @Schema(description = "메시지 ID", example = "projects/study-up-448918/messages/0:1621234567890123")
        String messageId,

        @Schema(description = "전송 시간", example = "2023-05-01T14:30:00")
        LocalDateTime timestamp
) {
    public static TestNotificationResponse from(String messageId) {
        return new TestNotificationResponse(messageId, LocalDateTime.now());
    }
}

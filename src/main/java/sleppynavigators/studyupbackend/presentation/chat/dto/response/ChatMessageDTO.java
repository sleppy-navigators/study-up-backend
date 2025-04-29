package sleppynavigators.studyupbackend.presentation.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.chat.SenderType;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Schema(description = "채팅 메시지")
public record ChatMessageDTO(
        @Schema(description = "메시지 ID", example = "1")
        @NotBlank String id,

        @Schema(description = "보낸 사람 ID", example = "1")
        @NotNull Long senderId,

        @Schema(description = "보낸 사람 타입", example = "USER")
        @NotNull SenderType senderType,

        @Schema(description = "메시지 내용", example = "안녕하세요!")
        @NotBlank String content,

        @Schema(description = "메시지 생성 시간", example = "2023-10-01T12:00:00Z")
        @NotNull ZonedDateTime createdAt
) {
    public static ChatMessageDTO from(ChatMessage message) {
        return new ChatMessageDTO(
                message.getId().toString(),
                message.getSenderId(),
                message.getSenderType(),
                message.getContent(),
                message.getCreatedAt().atZone(ZoneId.systemDefault())
        );
    }
} 

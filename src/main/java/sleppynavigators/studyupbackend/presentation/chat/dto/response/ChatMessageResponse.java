package sleppynavigators.studyupbackend.presentation.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.Builder;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.chat.SenderType;

@Builder
@Schema(description = "채팅 메시지 응답")
public record ChatMessageResponse(
        @Schema(description = "메시지 ID", example = "1")
        @NotBlank String id,

        @Schema(description = "그룹 ID", example = "1")
        @NotNull Long groupId,

        @Schema(description = "보낸 사람 ID", example = "1")
        @NotNull Long senderId,

        @Schema(description = "보낸 사람 타입", example = "USER")
        @NotNull SenderType senderType,

        @Schema(description = "메시지 내용", example = "안녕하세요!")
        @NotBlank String content,

        @Schema(description = "메시지 생성 시간", example = "2023-10-01T12:00:00Z")
        @NotNull ZonedDateTime createdAt
) {
    public static ChatMessageResponse from(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .id(chatMessage.getId().toString())
                .groupId(chatMessage.getGroupId())
                .senderId(chatMessage.getSenderId())
                .senderType(chatMessage.getSenderType())
                .content(chatMessage.getContent())
                .createdAt(chatMessage.getCreatedAt().atZone(ZoneId.systemDefault()))
                .build();
    }
}

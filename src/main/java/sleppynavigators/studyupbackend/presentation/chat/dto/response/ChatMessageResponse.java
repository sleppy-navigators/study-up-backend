package sleppynavigators.studyupbackend.presentation.chat.dto.response;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.chat.SenderType;

@Builder
public record ChatMessageResponse(
        @NotNull String id,
        @NotNull Long groupId,
        @NotNull Long senderId,
        @NotNull SenderType senderType,
        @NotBlank String content,
        @NotNull LocalDateTime timestamp
) {
    public static ChatMessageResponse from(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .id(chatMessage.getId().toString())
                .groupId(chatMessage.getGroupId())
                .senderId(chatMessage.getSenderId())
                .senderType(chatMessage.getSenderType())
                .content(chatMessage.getContent())
                .timestamp(chatMessage.getCreatedAt())
                .build();
    }
}

package sleppynavigators.studyupbackend.presentation.chat.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;

@Builder
public record ChatMessageResponse(
        String messageId,
        Long groupId,
        Long senderId,
        String content,
        LocalDateTime timestamp
) {
    public static ChatMessageResponse from(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .messageId(chatMessage.getId().toString())
                .groupId(chatMessage.getGroupId())
                .senderId(chatMessage.getSenderId())
                .content(chatMessage.getContent())
                .timestamp(chatMessage.getCreatedAt())
                .build();
    }
}

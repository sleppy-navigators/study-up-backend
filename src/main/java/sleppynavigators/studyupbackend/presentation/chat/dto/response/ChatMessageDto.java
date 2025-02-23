package sleppynavigators.studyupbackend.presentation.chat.dto.response;

import sleppynavigators.studyupbackend.domain.chat.ChatMessage;

import java.time.LocalDateTime;

public record ChatMessageDto(
    String id,
    Long senderId,
    String content,
    LocalDateTime createdAt
) {
    public static ChatMessageDto from(ChatMessage message) {
        return new ChatMessageDto(
            message.getId().toString(),
            message.getSenderId(),
            message.getContent(),
            message.getCreatedAt()
        );
    }
} 
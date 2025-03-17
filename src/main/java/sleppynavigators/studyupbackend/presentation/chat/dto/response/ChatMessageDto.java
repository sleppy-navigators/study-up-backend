package sleppynavigators.studyupbackend.presentation.chat.dto.response;

import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.chat.SenderType;

import java.time.LocalDateTime;

public record ChatMessageDto(
    String id,
    Long senderId,
    SenderType senderType,
    String content,
    LocalDateTime createdAt
) {
    public static ChatMessageDto from(ChatMessage message) {
        return new ChatMessageDto(
            message.getId().toString(),
            message.getSenderId(),
            message.getSenderType(),
            message.getContent(),
            message.getCreatedAt()
        );
    }
} 
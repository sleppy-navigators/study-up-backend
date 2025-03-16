package sleppynavigators.studyupbackend.presentation.chat.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.chat.SenderType;

@Builder
public record ChatMessageResponse(
        String id,
        Long groupId,
        Long senderId,
        SenderType senderType,
        String content,
        LocalDateTime timestamp
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

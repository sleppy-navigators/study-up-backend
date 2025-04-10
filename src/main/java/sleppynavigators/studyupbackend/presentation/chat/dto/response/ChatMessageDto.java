package sleppynavigators.studyupbackend.presentation.chat.dto.response;

import jakarta.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.chat.SenderType;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public record ChatMessageDto(
        @NotNull String id,
        @NotNull Long senderId,
        @NotNull SenderType senderType,
        @NotBlank String content,
        @NotNull ZonedDateTime createdAt
) {
    public static ChatMessageDto from(ChatMessage message) {
        return new ChatMessageDto(
                message.getId().toString(),
                message.getSenderId(),
                message.getSenderType(),
                message.getContent(),
                message.getCreatedAt().atZone(ZoneId.systemDefault())
        );
    }
} 

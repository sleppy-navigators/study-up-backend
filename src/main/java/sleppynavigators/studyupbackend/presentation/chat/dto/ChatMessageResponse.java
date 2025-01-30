package sleppynavigators.studyupbackend.presentation.chat.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatMessageResponse(
    Long groupId,
    Long senderId,
    String content,
    LocalDateTime timestamp
) {} 

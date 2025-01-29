package sleppynavigators.studyupbackend.presentation.chat.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageResponse {
    private Long groupId;
    private Long senderId;
    private String content;
    private LocalDateTime timestamp;
} 

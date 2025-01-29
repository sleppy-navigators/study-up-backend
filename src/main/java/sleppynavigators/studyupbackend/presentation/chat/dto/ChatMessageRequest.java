package sleppynavigators.studyupbackend.presentation.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageRequest {
    private Long groupId;
    private Long senderId;
    private String content;
} 
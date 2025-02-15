package sleppynavigators.studyupbackend.presentation.chat.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ChatMessageResponse(
        Long groupId,
        Long senderId,
        String content,
        LocalDateTime timestamp
) {
}

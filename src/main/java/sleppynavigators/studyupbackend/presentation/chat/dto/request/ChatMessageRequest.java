package sleppynavigators.studyupbackend.presentation.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "채팅 메시지 요청")
public record ChatMessageRequest(
        @Schema(description = "그룹 ID", example = "1")
        @NotNull
        Long groupId,

        @Schema(description = "메시지 내용", example = "안녕하세요")
        @NotNull
        @Size(min = 1, max = 1000, message = "메시지는 1자 이상 1000자 이하여야 합니다")
        String content
) {
}

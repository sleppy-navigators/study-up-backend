package sleppynavigators.studyupbackend.presentation.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.domain.Page;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;

@Schema(description = "채팅 메시지 목록 응답")
public record ChatMessageListResponse(
        @Schema(description = "채팅 메시지 목록")
        @NotNull @Valid List<ChatMessageDTO> messages,

        @Schema(description = "현재 페이지 번호", example = "1")
        @NotNull Integer currentPage,

        @Schema(description = "총 페이지 수", example = "10")
        @NotNull Integer pageCount,

        @Schema(description = "총 메시지 수", example = "100")
        @NotNull Long chatMessageCount
) {
    public static ChatMessageListResponse from(Page<ChatMessage> messagePage) {

        List<ChatMessageDTO> messages = messagePage.getContent().stream()
                .map(ChatMessageDTO::from)
                .toList();

        return new ChatMessageListResponse(
                messages,
                messagePage.getNumber(),
                messagePage.getTotalPages(),
                messagePage.getTotalElements()
        );
    }
} 

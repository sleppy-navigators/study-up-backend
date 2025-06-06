package sleppynavigators.studyupbackend.presentation.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.chat.SenderType;
import sleppynavigators.studyupbackend.domain.chat.action.ChatAction;
import sleppynavigators.studyupbackend.domain.chat.action.ChatActionType;

@Builder
@Schema(description = "채팅 메시지 응답")
public record ChatMessageResponse(
        @Schema(description = "메시지 ID", example = "1")
        @NotBlank String id,

        @Schema(description = "보낸 사람 ID", example = "1")
        @NotNull Long senderId,

        @Schema(description = "보낸 사람 타입", example = "USER")
        @NotNull SenderType senderType,

        @Schema(description = "메시지 내용", example = "안녕하세요!")
        @NotBlank String content,

        @Schema(description = "채팅 액션 아이템 목록")
        @NotNull List<ChatActionItem> chatActionList,

        @Schema(description = "메시지 생성 시간", example = "2023-10-01T12:00:00Z")
        @NotNull ZonedDateTime createdAt
) {

    @Schema(description = "채팅 액션 아이템")
    public record ChatActionItem(

            @Schema(description = "액션 타입", example = "HUNT_TASK")
            @NotBlank ChatActionType type,

            @Schema(description = "URL", example = "/users/1")
            @NotBlank String url,

            @Schema(description = "HTTP 메소드", example = "GET")
            @NotBlank String httpMethod
    ) {

        public static ChatActionItem fromEntity(ChatAction chatAction) {
            String httpMethod = chatAction.getHttpMethod() != null
                    ? chatAction.getHttpMethod().toString()
                    : null;
            return new ChatActionItem(
                    chatAction.getType(),
                    chatAction.getUrl(),
                    httpMethod);
        }
    }

    public static ChatMessageResponse from(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .id(chatMessage.getId().toString())
                .senderId(chatMessage.getSenderId())
                .senderType(chatMessage.getSenderType())
                .content(chatMessage.getContent())
                .chatActionList(chatMessage.getActionList().stream().map(ChatActionItem::fromEntity).toList())
                .createdAt(chatMessage.getCreatedAt().atZone(ZoneId.systemDefault()))
                .build();
    }
}

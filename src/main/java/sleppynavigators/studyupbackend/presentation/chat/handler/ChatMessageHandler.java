package sleppynavigators.studyupbackend.presentation.chat.handler;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import sleppynavigators.studyupbackend.application.chat.ChatMessageService;
import sleppynavigators.studyupbackend.presentation.authentication.filter.UserAuthentication;
import sleppynavigators.studyupbackend.presentation.chat.dto.ChatMessageRequest;

@Controller
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageHandler {

    private static final String GROUP_DESTINATION = "/topic/group/%s";
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat/message")
    public void handle(
            @Valid ChatMessageRequest message,
            UserAuthentication userAuthentication // WebSocket은 @AuthenticationPrincipal을 지원하지 않음
    ) {
        String destination = String.format(GROUP_DESTINATION, message.groupId());
        chatMessageService.sendMessage(message, destination, userAuthentication.getPrincipal().userId());
    }
}

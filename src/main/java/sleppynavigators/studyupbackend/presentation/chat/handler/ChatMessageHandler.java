package sleppynavigators.studyupbackend.presentation.chat.handler;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import sleppynavigators.studyupbackend.application.chat.ChatService;
import sleppynavigators.studyupbackend.presentation.chat.dto.ChatMessageRequest;

@Controller
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageHandler {

    private static final String GROUP_DESTINATION = "/topic/group/%s";
    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void handle(@Valid ChatMessageRequest message) {
        String destination = String.format(GROUP_DESTINATION, message.groupId());
        chatService.sendMessage(message, destination);
    }
}

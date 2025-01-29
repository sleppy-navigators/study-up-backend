package sleppynavigators.studyupbackend.presentation.chat.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import sleppynavigators.studyupbackend.presentation.chat.dto.WebSocketErrorResponse;
import sleppynavigators.studyupbackend.presentation.chat.exception.ChatMessageException;
import sleppynavigators.studyupbackend.presentation.common.APIResponse;
import sleppynavigators.studyupbackend.presentation.common.APIResult;

import java.security.Principal;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class WebSocketExceptionHandler {
    private static final String USER_ERROR_DESTINATION = "/user/queue/errors";
    private final SimpMessageSendingOperations messagingTemplate;

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidationException(MethodArgumentNotValidException ignored, Principal principal) {
        sendErrorToUser(principal.getName(), 
            new APIResponse<>(APIResult.BAD_REQUEST, "메시지 형식이 올바르지 않습니다"));
    }

    @MessageExceptionHandler(MessageDeliveryException.class)
    public void handleMessageDeliveryException(MessageDeliveryException exception, Principal principal) {
        log.error("Message delivery failed", exception);
        sendErrorToUser(principal.getName(),
            new APIResponse<>(APIResult.INTERNAL_SERVER_ERROR, "메시지 전송에 실패했습니다"));
    }

    @MessageExceptionHandler(ChatMessageException.class)
    public void handleChatMessageException(ChatMessageException exception, Principal principal) {
        if (APIResult.INTERNAL_SERVER_ERROR.equals(exception.getResult())) {
            log.error("Chat message error", exception);
        }
        sendErrorToUser(principal.getName(),
            new APIResponse<>(exception.getResult(), exception.getMessage()));
    }

    @MessageExceptionHandler(Exception.class)
    public void handleException(Exception exception, Principal principal) {
        log.error("Unexpected WebSocket error", exception);
        sendErrorToUser(principal.getName(),
            new APIResponse<>(APIResult.INTERNAL_SERVER_ERROR, "예기치 않은 오류가 발생했습니다"));
    }

    private void sendErrorToUser(String username, APIResponse<String> response) {
        WebSocketErrorResponse errorResponse = WebSocketErrorResponse.from(response);
        messagingTemplate.convertAndSendToUser(username, USER_ERROR_DESTINATION, errorResponse);
    }
} 
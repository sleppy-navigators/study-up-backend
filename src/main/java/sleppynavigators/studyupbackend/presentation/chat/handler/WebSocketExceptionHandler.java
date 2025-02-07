package sleppynavigators.studyupbackend.presentation.chat.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.ControllerAdvice;
import sleppynavigators.studyupbackend.exception.ErrorCode;
import sleppynavigators.studyupbackend.presentation.chat.exception.ChatMessageException;
import sleppynavigators.studyupbackend.presentation.common.SuccessResponse;

import java.security.Principal;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class WebSocketExceptionHandler {

    private static final String USER_ERROR_DESTINATION = "/user/queue/errors";
    private static final String PUBLIC_ERROR_DESTINATION = "/topic/errors";

    private final SimpMessageSendingOperations messagingTemplate;

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidationException(MethodArgumentNotValidException ignored, Principal principal) {
        sendError(principal, null); // new SuccessResponse<>(ErrorCode.BAD_REQUEST));
    }

    @MessageExceptionHandler(ChatMessageException.class)
    public void handleChatMessageException(ChatMessageException exception, Principal principal) {
        if (ErrorCode.INTERNAL_SERVER_ERROR.equals(exception.getResult())) {
            log.error("Chat message error", exception);
        }
        sendError(principal, null); // new SuccessResponse<>(exception.getResult()));
    }

    @MessageExceptionHandler(Exception.class)
    public void handleException(Exception exception, Principal principal) {
        log.error("Unexpected WebSocket error", exception);
        sendError(principal, null); // new SuccessResponse<>(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    private void sendError(Principal principal, SuccessResponse<?> response) {
        if (principal != null) {
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    USER_ERROR_DESTINATION,
                    response
            );
        } else {
            messagingTemplate.convertAndSend(PUBLIC_ERROR_DESTINATION, response);
        }
    }
}

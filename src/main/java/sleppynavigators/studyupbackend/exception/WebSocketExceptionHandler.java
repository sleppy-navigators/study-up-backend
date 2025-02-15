package sleppynavigators.studyupbackend.exception;

import java.security.Principal;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.ControllerAdvice;
import sleppynavigators.studyupbackend.exception.business.BusinessBaseException;
import sleppynavigators.studyupbackend.exception.network.InvalidApiException;

/**
 * 현재 실시간 메시지 처리 기준으로만 예외 처리를 수행하는 핸들러입니다.
 * TODO(@Jayon): 향후 푸시 알림 등 다른 실시간 처리가 추가될 경우 이 핸들러를 수정해야 합니다.
 */
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class WebSocketExceptionHandler {

    private static final String USER_ERROR_DESTINATION = "/queue/errors";
    private static final String PUBLIC_ERROR_DESTINATION = "/topic/errors";

    private final SimpMessageSendingOperations messagingTemplate;

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidationException(
            Message<?> message,
            MethodArgumentNotValidException exception,
            Principal principal
    ) {
        String requestPath = extractRequestPath(message);
        sendError(
                principal,
                ErrorResponse.ofWebSocketError(new InvalidApiException(exception.getMessage()), requestPath)
        );
    }

    @MessageExceptionHandler(BusinessBaseException.class)
    public void handleBusinessException(
            Message<?> message,
            BusinessBaseException exception,
            Principal principal
    ) {
        String requestPath = extractRequestPath(message);
        if (exception.getStatus() == ErrorCode.INTERNAL_SERVER_ERROR.getStatus()) {
            log.error("Business error at {}: {}", requestPath, exception.getMessage());
        }
        sendError(
                principal,
                ErrorResponse.ofWebSocketError(exception, requestPath)
        );
    }

    @MessageExceptionHandler(Exception.class)
    public void handleException(
            Message<?> message,
            Exception exception,
            Principal principal
    ) {
        String requestPath = extractRequestPath(message);
        log.error("Unexpected error at {}: {}", requestPath, exception.getMessage(), exception);
        sendError(
                principal,
                ErrorResponse.ofWebSocketError(new UnknownException(), requestPath)
        );
    }

    private String extractRequestPath(Message<?> message) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
        return accessor.getDestination();
    }

    private void sendError(Principal principal, ErrorResponse response) {
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

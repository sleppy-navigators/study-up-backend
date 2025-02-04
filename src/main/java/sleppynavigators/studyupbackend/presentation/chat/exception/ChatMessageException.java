package sleppynavigators.studyupbackend.presentation.chat.exception;

import lombok.Getter;
import sleppynavigators.studyupbackend.exception.ErrorCode;

@Getter
public class ChatMessageException extends RuntimeException {
    private final ErrorCode result;

    public ChatMessageException(String message) {
        this(ErrorCode.INVALID_API, message);
    }

    public ChatMessageException(ErrorCode result, String message) {
        super(message);
        this.result = result;
    }

    public ChatMessageException(String message, Throwable cause) {
        this(ErrorCode.INTERNAL_SERVER_ERROR, message, cause);
    }

    public ChatMessageException(ErrorCode result, String message, Throwable cause) {
        super(message, cause);
        this.result = result;
    }
}

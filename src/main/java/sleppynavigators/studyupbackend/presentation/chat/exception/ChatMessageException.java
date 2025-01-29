package sleppynavigators.studyupbackend.presentation.chat.exception;

import lombok.Getter;
import sleppynavigators.studyupbackend.presentation.common.APIResult;

@Getter
public class ChatMessageException extends RuntimeException {
    private final APIResult result;

    public ChatMessageException(String message) {
        this(APIResult.BAD_REQUEST, message);
    }

    public ChatMessageException(APIResult result, String message) {
        super(message);
        this.result = result;
    }

    public ChatMessageException(String message, Throwable cause) {
        this(APIResult.INTERNAL_SERVER_ERROR, message, cause);
    }

    public ChatMessageException(APIResult result, String message, Throwable cause) {
        super(message, cause);
        this.result = result;
    }
} 
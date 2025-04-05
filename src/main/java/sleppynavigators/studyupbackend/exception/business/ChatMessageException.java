package sleppynavigators.studyupbackend.exception.business;

import lombok.Getter;
import sleppynavigators.studyupbackend.exception.BaseException;
import sleppynavigators.studyupbackend.exception.ErrorCode;

@Getter
public class ChatMessageException extends BaseException {

    public ChatMessageException() {
        super(ErrorCode.CHAT_MESSAGE);
    }

    public ChatMessageException(String message) {
        super(ErrorCode.CHAT_MESSAGE, message);
    }

    public ChatMessageException(Throwable cause) {
        super(ErrorCode.CHAT_MESSAGE, cause);
    }

    public ChatMessageException(String message, Throwable cause) {
        super(ErrorCode.CHAT_MESSAGE, message, cause);
    }
}

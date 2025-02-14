package sleppynavigators.studyupbackend.exception.business;

import lombok.Getter;
import sleppynavigators.studyupbackend.exception.BaseException;
import sleppynavigators.studyupbackend.exception.ErrorCode;

@Getter
public class ChatMessageException extends BaseException {

    protected ChatMessageException(int status, String code, String message) {
        super(status, code, message);
    }

    public ChatMessageException(String message) {
        super(ErrorCode.CHAT_MESSAGE, message);
    }

    public ChatMessageException() {
        super(ErrorCode.CHAT_MESSAGE);
    }
}

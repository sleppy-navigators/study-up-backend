package sleppynavigators.studyupbackend.exception.client;

import sleppynavigators.studyupbackend.exception.BaseException;
import sleppynavigators.studyupbackend.exception.ErrorCode;

public class ClientBaseException extends BaseException {

    protected ClientBaseException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected ClientBaseException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected ClientBaseException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    protected ClientBaseException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}

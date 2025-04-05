package sleppynavigators.studyupbackend.exception.business;

import sleppynavigators.studyupbackend.exception.BaseException;
import sleppynavigators.studyupbackend.exception.ErrorCode;

public class BusinessBaseException extends BaseException {

    protected BusinessBaseException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected BusinessBaseException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected BusinessBaseException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    protected BusinessBaseException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}

package sleppynavigators.studyupbackend.exception.network;

import sleppynavigators.studyupbackend.exception.BaseException;
import sleppynavigators.studyupbackend.exception.ErrorCode;

public abstract class NetworkBaseException extends BaseException {

    protected NetworkBaseException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected NetworkBaseException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected NetworkBaseException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    protected NetworkBaseException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}

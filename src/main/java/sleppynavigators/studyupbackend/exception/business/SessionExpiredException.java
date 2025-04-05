package sleppynavigators.studyupbackend.exception.business;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class SessionExpiredException extends BusinessBaseException {

    public SessionExpiredException() {
        super(ErrorCode.SESSION_EXPIRED);
    }

    public SessionExpiredException(String message) {
        super(ErrorCode.SESSION_EXPIRED, message);
    }

    public SessionExpiredException(Throwable cause) {
        super(ErrorCode.SESSION_EXPIRED, cause);
    }

    public SessionExpiredException(String message, Throwable cause) {
        super(ErrorCode.SESSION_EXPIRED, message, cause);
    }
}

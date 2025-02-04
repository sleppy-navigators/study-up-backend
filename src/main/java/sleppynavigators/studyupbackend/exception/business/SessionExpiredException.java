package sleppynavigators.studyupbackend.exception.business;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class SessionExpiredException extends BusinessExceptionBase {

    protected SessionExpiredException(int status, String code, String message) {
        super(status, code, message);
    }

    public SessionExpiredException(String message) {
        super(ErrorCode.SESSION_EXPIRED, message);
    }

    public SessionExpiredException() {
        super(ErrorCode.SESSION_EXPIRED);
    }
}

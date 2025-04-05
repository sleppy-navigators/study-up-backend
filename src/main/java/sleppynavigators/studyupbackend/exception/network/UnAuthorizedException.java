package sleppynavigators.studyupbackend.exception.network;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class UnAuthorizedException extends NetworkBaseException {

    public UnAuthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }

    public UnAuthorizedException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }

    public UnAuthorizedException(Throwable cause) {
        super(ErrorCode.UNAUTHORIZED, cause);
    }

    public UnAuthorizedException(String message, Throwable cause) {
        super(ErrorCode.UNAUTHORIZED, message, cause);
    }
}

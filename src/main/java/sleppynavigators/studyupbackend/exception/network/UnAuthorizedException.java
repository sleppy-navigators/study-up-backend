package sleppynavigators.studyupbackend.exception.network;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class UnAuthorizedException extends NetworkBaseException {

    protected UnAuthorizedException(int status, String code, String message) {
        super(status, code, message);
    }

    public UnAuthorizedException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }

    public UnAuthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }
}

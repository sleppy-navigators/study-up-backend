package sleppynavigators.studyupbackend.exception.business;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class UnknownException extends BusinessBaseException {

    protected UnknownException(int status, String code, String message) {
        super(status, code, message);
    }

    public UnknownException(String message) {
        super(ErrorCode.INTERNAL_SERVER_ERROR, message);
    }

    public UnknownException() {
        super(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}

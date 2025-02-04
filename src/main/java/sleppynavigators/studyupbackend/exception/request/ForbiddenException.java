package sleppynavigators.studyupbackend.exception.request;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class ForbiddenException extends RequestExceptionBase {

    protected ForbiddenException(int status, String code, String message) {
        super(status, code, message);
    }

    public ForbiddenException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }

    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN);
    }
}

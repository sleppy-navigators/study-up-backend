package sleppynavigators.studyupbackend.exception.business;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class ForbiddenContentException extends BusinessBaseException {

    protected ForbiddenContentException(int status, String code, String message) {
        super(status, code, message);
    }

    public ForbiddenContentException(String message) {
        super(ErrorCode.FORBIDDEN_CONTENT, message);
    }

    public ForbiddenContentException() {
        super(ErrorCode.FORBIDDEN_CONTENT);
    }
}

package sleppynavigators.studyupbackend.exception.business;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class ForbiddenContentException extends BusinessBaseException {

    public ForbiddenContentException() {
        super(ErrorCode.FORBIDDEN_CONTENT);
    }

    public ForbiddenContentException(String message) {
        super(ErrorCode.FORBIDDEN_CONTENT, message);
    }

    public ForbiddenContentException(Throwable cause) {
        super(ErrorCode.FORBIDDEN_CONTENT, cause);
    }

    public ForbiddenContentException(String message, Throwable cause) {
        super(ErrorCode.FORBIDDEN_CONTENT, message, cause);
    }
}

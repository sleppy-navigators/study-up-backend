package sleppynavigators.studyupbackend.exception.request;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class InvalidCredentialException extends RequestExceptionBase {

    protected InvalidCredentialException(int status, String code, String message) {
        super(status, code, message);
    }

    public InvalidCredentialException(String message) {
        super(ErrorCode.INVALID_API, message);
    }

    public InvalidCredentialException() {
        super(ErrorCode.INVALID_API);
    }
}

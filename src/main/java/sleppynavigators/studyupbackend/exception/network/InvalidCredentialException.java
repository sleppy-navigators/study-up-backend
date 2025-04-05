package sleppynavigators.studyupbackend.exception.network;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class InvalidCredentialException extends NetworkBaseException {

    public InvalidCredentialException() {
        super(ErrorCode.INVALID_CREDENTIALS);
    }

    public InvalidCredentialException(String message) {
        super(ErrorCode.INVALID_CREDENTIALS, message);
    }

    public InvalidCredentialException(Throwable cause) {
        super(ErrorCode.INVALID_CREDENTIALS, cause);
    }

    public InvalidCredentialException(String message, Throwable cause) {
        super(ErrorCode.INVALID_CREDENTIALS, message, cause);
    }
}

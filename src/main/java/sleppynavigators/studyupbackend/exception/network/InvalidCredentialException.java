package sleppynavigators.studyupbackend.exception.network;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class InvalidCredentialException extends NetworkBaseException {

    protected InvalidCredentialException(int status, String code, String message) {
        super(status, code, message);
    }

    public InvalidCredentialException(String message) {
        super(ErrorCode.INVALID_CREDENTIALS, message);
    }

    public InvalidCredentialException() {
        super(ErrorCode.INVALID_CREDENTIALS);
    }
}

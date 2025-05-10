package sleppynavigators.studyupbackend.exception.client;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class InitializeFailedException extends ClientBaseException {

    public InitializeFailedException() {
        super(ErrorCode.INITIALIZE_FAILED);
    }

    public InitializeFailedException(String message) {
        super(ErrorCode.INITIALIZE_FAILED, message);
    }

    public InitializeFailedException(Throwable cause) {
        super(ErrorCode.INITIALIZE_FAILED, cause);
    }

    public InitializeFailedException(String message, Throwable cause) {
        super(ErrorCode.INITIALIZE_FAILED, message, cause);
    }
}

package sleppynavigators.studyupbackend.exception.business;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class InvalidPayloadException extends BusinessBaseException {

    public InvalidPayloadException() {
        super(ErrorCode.INVALID_PAYLOAD);
    }

    public InvalidPayloadException(String message) {
        super(ErrorCode.INVALID_PAYLOAD, message);
    }

    public InvalidPayloadException(Throwable cause) {
        super(ErrorCode.INVALID_PAYLOAD, cause);
    }

    public InvalidPayloadException(String message, Throwable cause) {
        super(ErrorCode.INVALID_PAYLOAD, message, cause);
    }
}

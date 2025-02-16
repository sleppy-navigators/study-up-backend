package sleppynavigators.studyupbackend.exception.business;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class InvalidPayloadException extends BusinessBaseException {

    protected InvalidPayloadException(int status, String code, String message) {
        super(status, code, message);
    }

    public InvalidPayloadException(String message) {
        super(ErrorCode.INVALID_PAYLOAD, message);
    }

    public InvalidPayloadException() {
        super(ErrorCode.INVALID_PAYLOAD);
    }
}

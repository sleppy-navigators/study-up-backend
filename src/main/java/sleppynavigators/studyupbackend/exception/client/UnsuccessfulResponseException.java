package sleppynavigators.studyupbackend.exception.client;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class UnsuccessfulResponseException extends ClientBaseException {

    public UnsuccessfulResponseException() {
        super(ErrorCode.UNSUCCESSFUL_RESPONSE);
    }

    public UnsuccessfulResponseException(String message) {
        super(ErrorCode.UNSUCCESSFUL_RESPONSE, message);
    }

    public UnsuccessfulResponseException(Throwable cause) {
        super(ErrorCode.UNSUCCESSFUL_RESPONSE, cause);
    }

    public UnsuccessfulResponseException(String message, Throwable cause) {
        super(ErrorCode.UNSUCCESSFUL_RESPONSE, message, cause);
    }
}

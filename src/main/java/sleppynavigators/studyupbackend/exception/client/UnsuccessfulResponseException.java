package sleppynavigators.studyupbackend.exception.client;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class UnsuccessfulResponseException extends ClientBaseException {

    protected UnsuccessfulResponseException(int status, String code, String message) {
        super(status, code, message);
    }

    public UnsuccessfulResponseException(String message) {
        super(ErrorCode.UNSUCCESSFUL_RESPONSE, message);
    }

    public UnsuccessfulResponseException() {
        super(ErrorCode.UNSUCCESSFUL_RESPONSE);
    }
}

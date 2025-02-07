package sleppynavigators.studyupbackend.exception.network;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class InvalidApiException extends NetworkBaseException {

    protected InvalidApiException(int status, String code, String message) {
        super(status, code, message);
    }

    public InvalidApiException(String message) {
        super(ErrorCode.INVALID_API, message);
    }

    public InvalidApiException() {
        super(ErrorCode.INVALID_API);
    }
}

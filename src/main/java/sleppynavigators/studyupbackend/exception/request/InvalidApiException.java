package sleppynavigators.studyupbackend.exception.request;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class InvalidApiException extends RequestExceptionBase {

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

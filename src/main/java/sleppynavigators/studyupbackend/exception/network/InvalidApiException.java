package sleppynavigators.studyupbackend.exception.network;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class InvalidApiException extends NetworkBaseException {

    public InvalidApiException() {
        super(ErrorCode.INVALID_API);
    }

    public InvalidApiException(String message) {
        super(ErrorCode.INVALID_API, message);
    }

    public InvalidApiException(Throwable cause) {
        super(ErrorCode.INVALID_API, cause);
    }

    public InvalidApiException(String message, Throwable cause) {
        super(ErrorCode.INVALID_API, message, cause);
    }
}

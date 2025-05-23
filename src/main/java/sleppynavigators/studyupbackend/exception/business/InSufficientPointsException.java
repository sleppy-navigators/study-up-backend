package sleppynavigators.studyupbackend.exception.business;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class InSufficientPointsException extends BusinessBaseException {

    public InSufficientPointsException() {
        super(ErrorCode.INSUFFICIENT_POINTS);
    }

    public InSufficientPointsException(String message) {
        super(ErrorCode.INSUFFICIENT_POINTS, message);
    }

    public InSufficientPointsException(Throwable cause) {
        super(ErrorCode.INSUFFICIENT_POINTS, cause);
    }

    public InSufficientPointsException(String message, Throwable cause) {
        super(ErrorCode.INSUFFICIENT_POINTS, message, cause);
    }
}

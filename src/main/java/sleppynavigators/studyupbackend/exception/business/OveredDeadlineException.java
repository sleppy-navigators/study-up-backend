package sleppynavigators.studyupbackend.exception.business;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class OveredDeadlineException extends BusinessBaseException {

    public OveredDeadlineException() {
        super(ErrorCode.OVERED_DEADLINE);
    }

    public OveredDeadlineException(String message) {
        super(ErrorCode.OVERED_DEADLINE, message);
    }

    public OveredDeadlineException(Throwable cause) {
        super(ErrorCode.OVERED_DEADLINE, cause);
    }

    public OveredDeadlineException(String message, Throwable cause) {
        super(ErrorCode.OVERED_DEADLINE, message, cause);
    }
}

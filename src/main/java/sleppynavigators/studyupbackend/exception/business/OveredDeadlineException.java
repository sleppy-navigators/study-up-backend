package sleppynavigators.studyupbackend.exception.business;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class OveredDeadlineException extends BusinessBaseException {

    protected OveredDeadlineException(int status, String code, String message) {
        super(status, code, message);
    }

    public OveredDeadlineException(String message) {
        super(ErrorCode.OVERED_DEADLINE, message);
    }

    public OveredDeadlineException() {
        super(ErrorCode.OVERED_DEADLINE);
    }
}

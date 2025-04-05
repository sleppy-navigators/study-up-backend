package sleppynavigators.studyupbackend.exception.business;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class ActionRequiredBeforeException extends BusinessBaseException {

    public ActionRequiredBeforeException() {
        super(ErrorCode.ACTION_REQUIRED_BEFORE);
    }

    public ActionRequiredBeforeException(String message) {
        super(ErrorCode.ACTION_REQUIRED_BEFORE, message);
    }

    public ActionRequiredBeforeException(Throwable cause) {
        super(ErrorCode.ACTION_REQUIRED_BEFORE, cause);
    }

    public ActionRequiredBeforeException(String message, Throwable cause) {
        super(ErrorCode.ACTION_REQUIRED_BEFORE, message, cause);
    }
}

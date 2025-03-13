package sleppynavigators.studyupbackend.exception.business;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class ActionRequiredBeforeException extends BusinessBaseException {

    public ActionRequiredBeforeException(int status, String code, String message) {
        super(status, code, message);
    }

    public ActionRequiredBeforeException(String message) {
        super(ErrorCode.ACTION_REQUIRED_BEFORE, message);
    }

    public ActionRequiredBeforeException() {
        super(ErrorCode.ACTION_REQUIRED_BEFORE);
    }
}

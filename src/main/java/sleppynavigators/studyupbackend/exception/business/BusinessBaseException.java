package sleppynavigators.studyupbackend.exception.business;

import sleppynavigators.studyupbackend.exception.BaseException;
import sleppynavigators.studyupbackend.exception.ErrorCode;

public class BusinessBaseException extends BaseException {

    protected BusinessBaseException(int status, String code, String message) {
        super(status, code, message);
    }

    protected BusinessBaseException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected BusinessBaseException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

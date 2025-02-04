package sleppynavigators.studyupbackend.exception.business;

import sleppynavigators.studyupbackend.exception.ErrorCode;
import sleppynavigators.studyupbackend.exception.ExceptionBase;

public class BusinessExceptionBase extends ExceptionBase {

    protected BusinessExceptionBase(int status, String code, String message) {
        super(status, code, message);
    }

    protected BusinessExceptionBase(ErrorCode errorCode) {
        super(errorCode);
    }

    protected BusinessExceptionBase(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

package sleppynavigators.studyupbackend.exception.database;

import sleppynavigators.studyupbackend.exception.ErrorCode;
import sleppynavigators.studyupbackend.exception.ExceptionBase;

public class DatabaseExceptionBase extends ExceptionBase {

    protected DatabaseExceptionBase(int status, String code, String message) {
        super(status, code, message);
    }

    protected DatabaseExceptionBase(ErrorCode errorCode) {
        super(errorCode);
    }

    protected DatabaseExceptionBase(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

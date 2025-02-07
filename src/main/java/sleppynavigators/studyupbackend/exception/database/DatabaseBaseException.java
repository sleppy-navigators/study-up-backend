package sleppynavigators.studyupbackend.exception.database;

import sleppynavigators.studyupbackend.exception.ErrorCode;
import sleppynavigators.studyupbackend.exception.BaseException;

public class DatabaseBaseException extends BaseException {

    protected DatabaseBaseException(int status, String code, String message) {
        super(status, code, message);
    }

    protected DatabaseBaseException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected DatabaseBaseException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

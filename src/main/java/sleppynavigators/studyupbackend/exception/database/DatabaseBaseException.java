package sleppynavigators.studyupbackend.exception.database;

import sleppynavigators.studyupbackend.exception.BaseException;
import sleppynavigators.studyupbackend.exception.ErrorCode;

public class DatabaseBaseException extends BaseException {

    protected DatabaseBaseException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected DatabaseBaseException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected DatabaseBaseException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    protected DatabaseBaseException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}

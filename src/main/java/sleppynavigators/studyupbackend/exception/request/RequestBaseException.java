package sleppynavigators.studyupbackend.exception.request;

import sleppynavigators.studyupbackend.exception.ErrorCode;
import sleppynavigators.studyupbackend.exception.BaseException;

public class RequestBaseException extends BaseException {

    protected RequestBaseException(int status, String code, String message) {
        super(status, code, message);
    }

    protected RequestBaseException(ErrorCode errorCode) {
        this(errorCode.getStatus(), errorCode.getCode(), errorCode.getDefaultMessage());
    }

    protected RequestBaseException(ErrorCode errorCode, String message) {
        this(errorCode.getStatus(), errorCode.getCode(), message);
    }
}

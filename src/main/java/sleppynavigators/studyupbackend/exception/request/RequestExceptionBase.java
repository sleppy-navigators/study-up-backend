package sleppynavigators.studyupbackend.exception.request;

import sleppynavigators.studyupbackend.exception.ErrorCode;
import sleppynavigators.studyupbackend.exception.ExceptionBase;

public class RequestExceptionBase extends ExceptionBase {
    
    protected RequestExceptionBase(int status, String code, String message) {
        super(status, code, message);
    }

    protected RequestExceptionBase(ErrorCode errorCode) {
        this(errorCode.getStatus(), errorCode.getCode(), errorCode.getDefaultMessage());
    }

    protected RequestExceptionBase(ErrorCode errorCode, String message) {
        this(errorCode.getStatus(), errorCode.getCode(), message);
    }
}

package sleppynavigators.studyupbackend.exception.network;

import sleppynavigators.studyupbackend.exception.BaseException;
import sleppynavigators.studyupbackend.exception.ErrorCode;

public class NetworkBaseException extends BaseException {

    protected NetworkBaseException(int status, String code, String message) {
        super(status, code, message);
    }

    protected NetworkBaseException(ErrorCode errorCode) {
        this(errorCode.getStatus(), errorCode.getCode(), errorCode.getDefaultMessage());
    }

    protected NetworkBaseException(ErrorCode errorCode, String message) {
        this(errorCode.getStatus(), errorCode.getCode(), message);
    }
}

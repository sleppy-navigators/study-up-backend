package sleppynavigators.studyupbackend.exception.client;

import sleppynavigators.studyupbackend.exception.BaseException;
import sleppynavigators.studyupbackend.exception.ErrorCode;

public class ClientBaseException extends BaseException {

    protected ClientBaseException(int status, String code, String message) {
        super(status, code, message);
    }

    protected ClientBaseException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected ClientBaseException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

package sleppynavigators.studyupbackend.exception.client;

import sleppynavigators.studyupbackend.exception.ErrorCode;
import sleppynavigators.studyupbackend.exception.ExceptionBase;

public class ClientExceptionBase extends ExceptionBase {

    protected ClientExceptionBase(int status, String code, String message) {
        super(status, code, message);
    }

    protected ClientExceptionBase(ErrorCode errorCode) {
        super(errorCode);
    }

    protected ClientExceptionBase(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

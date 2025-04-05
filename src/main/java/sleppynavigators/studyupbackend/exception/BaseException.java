package sleppynavigators.studyupbackend.exception;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {

    private final int status;
    private final String code;

    protected BaseException(ErrorCode errorCode) {
        this(errorCode, errorCode.getDefaultMessage());
    }

    protected BaseException(ErrorCode errorCode, String message) {
        super(message);
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
    }

    protected BaseException(ErrorCode errorCode, Throwable cause) {
        this(errorCode, errorCode.getDefaultMessage(), cause);
    }

    protected BaseException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
    }
}

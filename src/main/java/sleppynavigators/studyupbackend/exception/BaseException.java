package sleppynavigators.studyupbackend.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseException extends RuntimeException {

    protected final int status;
    protected final String code;
    protected final String message;

    protected BaseException(ErrorCode errorCode) {
        this(errorCode.getStatus(), errorCode.getCode(), errorCode.getDefaultMessage());
    }

    protected BaseException(ErrorCode errorCode, String message) {
        this(errorCode.getStatus(), errorCode.getCode(), message);
    }
}

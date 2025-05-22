package sleppynavigators.studyupbackend.exception.database;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class LockFailedException extends DatabaseBaseException {

    public LockFailedException() {
        super(ErrorCode.LOCK_FAILED);
    }

    public LockFailedException(String message) {
        super(ErrorCode.LOCK_FAILED, message);
    }

    public LockFailedException(Throwable cause) {
        super(ErrorCode.LOCK_FAILED, cause);
    }

    public LockFailedException(String message, Throwable cause) {
        super(ErrorCode.LOCK_FAILED, message, cause);
    }
}

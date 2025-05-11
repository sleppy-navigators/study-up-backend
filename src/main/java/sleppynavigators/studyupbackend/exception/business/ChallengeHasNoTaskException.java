package sleppynavigators.studyupbackend.exception.business;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class ChallengeHasNoTaskException extends BusinessBaseException {

    public ChallengeHasNoTaskException() {
        super(ErrorCode.HAS_NO_TASK);
    }

    public ChallengeHasNoTaskException(String message) {
        super(ErrorCode.HAS_NO_TASK, message);
    }

    public ChallengeHasNoTaskException(Throwable cause) {
        super(ErrorCode.HAS_NO_TASK, cause);
    }

    public ChallengeHasNoTaskException(String message, Throwable cause) {
        super(ErrorCode.HAS_NO_TASK, message, cause);
    }
}

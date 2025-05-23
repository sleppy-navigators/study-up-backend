package sleppynavigators.studyupbackend.exception.business;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class ChallengeInProgressException extends BusinessBaseException {

    public ChallengeInProgressException() {
        super(ErrorCode.CHALLENGE_IN_PROGRESS);
    }

    public ChallengeInProgressException(String message) {
        super(ErrorCode.CHALLENGE_IN_PROGRESS, message);
    }

    public ChallengeInProgressException(String message, Throwable cause) {
        super(ErrorCode.CHALLENGE_IN_PROGRESS, message, cause);
    }

    public ChallengeInProgressException(Throwable cause) {
        super(ErrorCode.CHALLENGE_IN_PROGRESS, cause);
    }
}

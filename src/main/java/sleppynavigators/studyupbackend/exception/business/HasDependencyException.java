package sleppynavigators.studyupbackend.exception.business;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class HasDependencyException extends BusinessBaseException {

    public HasDependencyException(int status, String code, String message) {
        super(status, code, message);
    }

    public HasDependencyException(String message) {
        super(ErrorCode.HAS_DEPENDENCY, message);
    }

    public HasDependencyException() {
        super(ErrorCode.HAS_DEPENDENCY);
    }
}

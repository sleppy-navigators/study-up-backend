package sleppynavigators.studyupbackend.exception;

public class UnknownException extends BaseException {

    public UnknownException() {
        super(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}

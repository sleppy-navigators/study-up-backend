package sleppynavigators.studyupbackend.exception.database;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class EntityNotFoundException extends DatabaseExceptionBase {

    protected EntityNotFoundException(int status, String code, String message) {
        super(status, code, message);
    }

    public EntityNotFoundException(String message) {
        super(ErrorCode.ENTITY_NOT_FOUND, message);
    }

    public EntityNotFoundException() {
        super(ErrorCode.ENTITY_NOT_FOUND);
    }
}

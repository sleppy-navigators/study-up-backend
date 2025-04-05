package sleppynavigators.studyupbackend.exception.database;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class EntityNotFoundException extends DatabaseBaseException {

    public EntityNotFoundException() {
        super(ErrorCode.ENTITY_NOT_FOUND);
    }

    public EntityNotFoundException(String message) {
        super(ErrorCode.ENTITY_NOT_FOUND, message);
    }

    public EntityNotFoundException(Throwable cause) {
        super(ErrorCode.ENTITY_NOT_FOUND, cause);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(ErrorCode.ENTITY_NOT_FOUND, message, cause);
    }
}

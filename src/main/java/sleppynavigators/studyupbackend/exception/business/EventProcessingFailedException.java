package sleppynavigators.studyupbackend.exception.business;

import sleppynavigators.studyupbackend.exception.ErrorCode;

public class EventProcessingFailedException extends BusinessBaseException {

    public EventProcessingFailedException() {
        super(ErrorCode.EVENT_PROCESSING_FAILED);
    }

    public EventProcessingFailedException(String message) {
        super(ErrorCode.EVENT_PROCESSING_FAILED, message);
    }

    public EventProcessingFailedException(Throwable cause) {
        super(ErrorCode.EVENT_PROCESSING_FAILED, cause);
    }

    public EventProcessingFailedException(String message, Throwable cause) {
        super(ErrorCode.EVENT_PROCESSING_FAILED, message, cause);
    }
}

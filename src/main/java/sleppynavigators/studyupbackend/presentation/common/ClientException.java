package sleppynavigators.studyupbackend.presentation.common;

/**
 * Exception for client errors(4xx).
 */
public class ClientException extends RuntimeException {

    public ClientException(String message) {
        super(message);
    }

    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }
}

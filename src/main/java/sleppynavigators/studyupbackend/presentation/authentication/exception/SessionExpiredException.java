package sleppynavigators.studyupbackend.presentation.authentication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import sleppynavigators.studyupbackend.presentation.common.ClientException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SessionExpiredException extends ClientException {
    public SessionExpiredException() {
        super("Session expired. Please sign in again.");
    }
}

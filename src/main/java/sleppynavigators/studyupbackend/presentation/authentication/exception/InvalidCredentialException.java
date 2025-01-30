package sleppynavigators.studyupbackend.presentation.authentication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import sleppynavigators.studyupbackend.presentation.common.ClientException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCredentialException extends ClientException {
    public InvalidCredentialException() {
        super("Invalid credentials");
    }
}

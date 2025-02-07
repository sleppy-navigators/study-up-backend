package sleppynavigators.studyupbackend.exception;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {

    protected final String code;
    protected final String message;
    private final String requestUrl;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public static ResponseEntity<ErrorResponse> toResponseEntity(BaseException exception, String requestUrl) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(new ErrorResponse(exception.code, exception.message, requestUrl));
    }
}

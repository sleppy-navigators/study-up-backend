package sleppynavigators.studyupbackend.exception;

import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {

    private final String code;
    private final String message;
    private final String requestUrl;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public static ResponseEntity<ErrorResponse> toResponseEntity(BaseException exception, String requestUrl) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(new ErrorResponse(exception.getCode(), exception.getMessage(), requestUrl));
    }

    public static ErrorResponse ofWebSocketError(BaseException exception, @Nullable String requestUrl) {
        return new ErrorResponse(exception.getCode(), exception.getMessage(), requestUrl);
    }
}

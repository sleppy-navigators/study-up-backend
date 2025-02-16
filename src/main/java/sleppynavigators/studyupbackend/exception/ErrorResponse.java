package sleppynavigators.studyupbackend.exception;

import jakarta.annotation.Nullable;
import java.time.LocalDateTime;

import lombok.*;
import org.springframework.http.ResponseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private String code;
    private String message;
    private String requestUrl;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponse(String code, String message, String requestUrl) {
        this.code = code;
        this.message = message;
        this.requestUrl = requestUrl;
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(BaseException exception, String requestUrl) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(new ErrorResponse(exception.getCode(), exception.getMessage(), requestUrl));
    }

    public static ErrorResponse ofWebSocketError(BaseException exception, @Nullable String requestUrl) {
        return new ErrorResponse(exception.getCode(), exception.getMessage(), requestUrl);
    }
}

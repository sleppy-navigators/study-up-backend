package sleppynavigators.studyupbackend.exception;

import jakarta.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

import lombok.*;
import org.springframework.http.ResponseEntity;

@Getter
public class ErrorResponse {

    private final String code;
    private final String message;
    private final String requestUrl;
    private final LocalDateTime timestamp = LocalDateTime.now();

    @JsonCreator
    public ErrorResponse(
            @JsonProperty("code") String code,
            @JsonProperty("message") String message,
            @JsonProperty("requestUrl") String requestUrl
    ) {
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

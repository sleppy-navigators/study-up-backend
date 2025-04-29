package sleppynavigators.studyupbackend.exception;

import jakarta.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@Schema(description = "API 오류 응답")
public class ErrorResponse {

    @Schema(description = "오류 코드", example = "R40")
    @NotBlank
    private final String code;

    @Schema(description = "오류 메시지", example = "Bad request")
    @NotBlank
    private final String message;

    @Schema(description = "요청 URL", example = "/api/users")
    @NotBlank
    private final String requestUrl;

    @Schema(description = "타임스탬프", example = "2025-04-27T14:30:15.02.014545487")
    @NotNull
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

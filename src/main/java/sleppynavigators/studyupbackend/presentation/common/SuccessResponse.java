package sleppynavigators.studyupbackend.presentation.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@Schema(description = "API 성공 응답")
public class SuccessResponse<T> {

    private static final int DEFAULT_SUCCESS_CODE = 200;
    private static final String DEFAULT_SUCCESS_MESSAGE = "Query Success";

    @Schema(description = "성공 메시지", example = "Query Success")
    @NotBlank
    private final String message;

    @Schema(description = "응답 데이터")
    @Valid
    private final T data;

    @JsonCreator
    public SuccessResponse(
            @JsonProperty("message") String message,
            @JsonProperty("data") T data
    ) {
        this.message = message;
        this.data = data;
    }

    public SuccessResponse(T data) {
        this(DEFAULT_SUCCESS_MESSAGE, data);
    }

    public static <T> ResponseEntity<SuccessResponse<T>> toResponseEntity(int code, String message, T data) {
        return ResponseEntity
                .status(code)
                .body(new SuccessResponse<>(message, data));
    }

    public static <T> ResponseEntity<SuccessResponse<T>> toResponseEntity(int code, T data) {
        return SuccessResponse.toResponseEntity(code, DEFAULT_SUCCESS_MESSAGE, data);
    }

    public static <T> ResponseEntity<SuccessResponse<T>> toResponseEntity(T data) {
        return SuccessResponse.toResponseEntity(DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, data);
    }
}

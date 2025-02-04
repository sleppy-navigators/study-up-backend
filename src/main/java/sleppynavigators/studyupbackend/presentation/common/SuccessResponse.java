package sleppynavigators.studyupbackend.presentation.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@Getter
@RequiredArgsConstructor
public class SuccessResponse<T> {

    private final String code;
    private final String message;
    private final T data;

    public static <T> ResponseEntity<SuccessResponse<T>> toResponseEntity(
            SuccessCode successCode, String message, T data) {
        return ResponseEntity
                .status(successCode.getStatus())
                .body(new SuccessResponse<>(successCode.getCode(), message, data));
    }

    public static <T> ResponseEntity<SuccessResponse<T>> toResponseEntity(SuccessCode successCode, T data) {
        return SuccessResponse.toResponseEntity(successCode, successCode.getDefaultMessage(), data);
    }
}

package sleppynavigators.studyupbackend.presentation.chat.dto;

import lombok.Builder;
import lombok.Getter;
import sleppynavigators.studyupbackend.presentation.common.APIResponse;
import sleppynavigators.studyupbackend.presentation.common.APIResult;

import java.time.LocalDateTime;

@Getter
@Builder
public class WebSocketErrorResponse {
    private final String code;
    private final String message;
    private final String detail;
    private final LocalDateTime timestamp;

    public static WebSocketErrorResponse from(APIResponse<String> response) {
        APIResult result = response.apiResult();
        return WebSocketErrorResponse.builder()
                .code(result.getCode())
                .message(result.getMessage())
                .detail(response.data())
                .timestamp(LocalDateTime.now())
                .build();
    }
} 
package sleppynavigators.studyupbackend.presentation.chat.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import sleppynavigators.studyupbackend.presentation.common.APIResponse;
import sleppynavigators.studyupbackend.presentation.common.APIResult;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WebSocketErrorResponse {
    
    private String code;
    private String message;
    private LocalDateTime timestamp;

    public static WebSocketErrorResponse from(APIResponse<String> response) {
        APIResult result = response.apiResult();

        return WebSocketErrorResponse.builder()
                .code(result.getCode())
                .message(result.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}

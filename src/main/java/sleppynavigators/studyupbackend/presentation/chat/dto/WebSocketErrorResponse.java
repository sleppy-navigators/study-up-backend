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
    private String detail;
    private LocalDateTime timestamp;

    public static WebSocketErrorResponse from(APIResponse<String> response) {
        APIResult result = response.apiResult();
        String message = response.data() != null ? response.data() : result.getMessage();
        
        return WebSocketErrorResponse.builder()
                .code(result.getCode())
                .message(message)
                .detail(null)
                .timestamp(LocalDateTime.now())
                .build();
    }
} 
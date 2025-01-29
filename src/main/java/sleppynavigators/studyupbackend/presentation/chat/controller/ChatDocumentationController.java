package sleppynavigators.studyupbackend.presentation.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sleppynavigators.studyupbackend.presentation.chat.dto.ChatMessageRequest;
import sleppynavigators.studyupbackend.presentation.chat.dto.ChatMessageResponse;
import sleppynavigators.studyupbackend.presentation.common.APIResponse;
import sleppynavigators.studyupbackend.presentation.common.APIResult;

@Tag(name = "Chat WebSocket", description = "채팅 웹소켓 통신 명세")
@RestController
@RequestMapping("/docs/chat")
public class ChatDocumentationController {

    @Operation(
        summary = "웹소켓 연결 정보",
        description = """
            웹소켓 연결 엔드포인트: /ws
            STOMP 설정:
            - 메시지 발행 prefix: /app
            - 구독 prefix: /topic
            
            연결 예시 (SockJS):
            ```javascript
            const socket = new SockJS('/ws');
            const stompClient = Stomp.over(socket);
            ```
            """
    )
    @GetMapping("/connection")
    public APIResponse<Void> connection() {
        return new APIResponse<>(APIResult.QUERY_OK);
    }

    @Operation(
        summary = "채팅 메시지 송신",
        description = "Websocket End-Point: /app/chat/message"
    )
    @GetMapping("/send")
    public APIResponse<Void> messageSend(ChatMessageRequest request) {
        return new APIResponse<>(APIResult.QUERY_OK);
    }

    @Operation(
        summary = "채팅 메시지 수신",
        description = "구독 주소: /topic/group/{groupId}"
    )
    @GetMapping("/subscribe")
    public APIResponse<ChatMessageResponse> messageSubscribe() {
        return new APIResponse<>(APIResult.QUERY_OK);
    }

    @Operation(
        summary = "에러 메시지 수신",
        description = "구독 주소(유저별): /user/queue/errors, 구독 주소(글로벌): /topic/errors"
    )
    @GetMapping("/error")
    public APIResponse<?> error() {
        return new APIResponse<>(APIResult.BAD_REQUEST);
    }
}

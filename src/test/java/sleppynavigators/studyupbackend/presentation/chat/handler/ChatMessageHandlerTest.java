package sleppynavigators.studyupbackend.presentation.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.ActiveProfiles;
import sleppynavigators.studyupbackend.presentation.chat.dto.ChatMessageRequest;
import sleppynavigators.studyupbackend.presentation.chat.dto.ChatMessageResponse;
import sleppynavigators.studyupbackend.presentation.chat.dto.WebSocketErrorResponse;
import sleppynavigators.studyupbackend.presentation.chat.support.WebSocketTestSupport;
import sleppynavigators.studyupbackend.presentation.common.APIResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("ChatMessageHandler 통합 테스트")
class ChatMessageHandlerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private WebSocketTestSupport webSocketTestSupport;
    private StompSession stompSession;

    @BeforeEach
    void setup() throws Exception {
        webSocketTestSupport = new WebSocketTestSupport(
                String.format("ws://localhost:%d/ws", port),
                objectMapper
        );
        webSocketTestSupport.connect();
        this.stompSession = webSocketTestSupport.getStompSession();
    }

    @AfterEach
    void cleanup() {
        webSocketTestSupport.disconnect();
    }

    @Test
    @DisplayName("채팅 메시지를 성공적으로 전송하고 수신한다")
    void whenValidMessage_thenMessageIsDelivered() throws Exception {
        // given
        Long groupId = 1L;
        String destination = webSocketTestSupport.getGroupDestination(groupId);
        CompletableFuture<ChatMessageResponse> future = webSocketTestSupport.subscribeAndReceive(
                destination, 
                ChatMessageResponse.class
        );

        ChatMessageRequest request = ChatMessageRequest.builder()
                .groupId(groupId)
                .senderId(1L)
                .content("테스트 메시지")
                .build();

        // when
        stompSession.send(webSocketTestSupport.getSendEndpoint(), request);

        // then
        ChatMessageResponse response = future.get(10, TimeUnit.SECONDS);
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo("테스트 메시지");
        assertThat(response.getSenderId()).isEqualTo(1L);
        assertThat(response.getGroupId()).isEqualTo(groupId);
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("메시지 내용이 없는 경우 예외가 발생한다")
    void whenEmptyContent_thenThrowsException() throws Exception {
        // given
        CompletableFuture<WebSocketErrorResponse> errorFuture = webSocketTestSupport.subscribeToErrors();

        ChatMessageRequest request = ChatMessageRequest.builder()
                .groupId(1L)
                .senderId(1L)
                .content("")
                .build();

        // when
        stompSession.send(webSocketTestSupport.getSendEndpoint(), request);

        // then
        WebSocketErrorResponse error = errorFuture.get(5, TimeUnit.SECONDS);
        assertThat(error.getCode()).isEqualTo(APIResult.BAD_REQUEST.getCode());
    }

    @Test
    @DisplayName("메시지 길이가 제한을 초과하면 예외가 발생한다")
    void whenContentTooLong_thenThrowsException() throws Exception {
        // given
        CompletableFuture<WebSocketErrorResponse> errorFuture = webSocketTestSupport.subscribeToErrors();
        
        String longContent = "a".repeat(1001); // 1000자 제한
        ChatMessageRequest request = ChatMessageRequest.builder()
                .groupId(1L)
                .senderId(1L)
                .content(longContent)
                .build();

        // when
        stompSession.send(webSocketTestSupport.getSendEndpoint(), request);

        // then
        WebSocketErrorResponse error = errorFuture.get(5, TimeUnit.SECONDS);
        assertThat(error.getCode()).isEqualTo(APIResult.BAD_REQUEST.getCode());
    }
}
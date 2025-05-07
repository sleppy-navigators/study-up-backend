package sleppynavigators.studyupbackend.presentation.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.simp.stomp.ConnectionLostException;
import org.springframework.messaging.simp.stomp.StompSession;
import sleppynavigators.studyupbackend.common.ApplicationBaseTest;
import sleppynavigators.studyupbackend.common.support.AuthSupport;
import sleppynavigators.studyupbackend.common.support.UserSupport;
import sleppynavigators.studyupbackend.common.support.WebSocketTestSupport;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.ErrorCode;
import sleppynavigators.studyupbackend.exception.ErrorResponse;
import sleppynavigators.studyupbackend.infrastructure.chat.ChatMessageRepository;
import sleppynavigators.studyupbackend.presentation.chat.dto.request.ChatMessageRequest;
import sleppynavigators.studyupbackend.presentation.chat.dto.response.ChatMessageResponse;
import sleppynavigators.studyupbackend.presentation.common.SuccessResponse;

@DisplayName("[프레젠테이션] ChatMessageHandler 통합 테스트")
class ChatMessageHandlerTest extends ApplicationBaseTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private AuthSupport authSupport;

    private WebSocketTestSupport webSocketTestSupport;
    private StompSession stompSession;
    private User testUser;

    @BeforeEach
    void setup() throws Exception {
        testUser = userSupport.registerUserToDB();
        String accessToken = authSupport.createAccessToken(testUser);

        String wsUrl = String.format("ws://localhost:%d/ws", port);
        webSocketTestSupport = new WebSocketTestSupport(wsUrl, objectMapper, accessToken);
        webSocketTestSupport.connect();
        this.stompSession = webSocketTestSupport.getStompSession();
    }

    @AfterEach
    void cleanup() {
        if (webSocketTestSupport != null) {
            webSocketTestSupport.disconnect();
        }
    }

    @Test
    @DisplayName("채팅 메시지 전송 및 저장 - 성공")
    void sendMessage_Success() throws Exception {
        // given
        Long groupId = 1L;
        String destination = webSocketTestSupport.getGroupDestination(groupId);
        CompletableFuture<SuccessResponse<ChatMessageResponse>> future = webSocketTestSupport.subscribeAndReceive(
                destination,
                new ParameterizedTypeReference<>() {
                });

        ChatMessageRequest request = ChatMessageRequest.builder()
                .groupId(groupId)
                .content("테스트 메시지")
                .build();

        // when
        stompSession.send(webSocketTestSupport.getSendEndpoint(), request);

        // then
        ChatMessageResponse response = future.get(10, TimeUnit.SECONDS).getData();
        assertThat(response.content()).isEqualTo("테스트 메시지");
        assertThat(response.senderId()).isEqualTo(testUser.getId());
        assertThat(response.groupId()).isEqualTo(groupId);
        assertThat(response.createdAt()).isNotNull();

        // MongoDB에 저장된 메시지 검증
        assertThat(chatMessageRepository.findById(new ObjectId(response.id()))).isPresent()
                .hasValueSatisfying(message -> {
                    assertThat(message.getContent()).isEqualTo("테스트 메시지");
                    assertThat(message.getSenderId()).isEqualTo(testUser.getId());
                    assertThat(message.getGroupId()).isEqualTo(groupId);
                    assertThat(message.getId().toString()).isEqualTo(response.id());
                });
    }

    @Test
    @DisplayName("메시지 전송 - 빈 내용")
    void sendMessage_EmptyContent_Fail() throws Exception {
        // given
        CompletableFuture<ErrorResponse> errorFuture = webSocketTestSupport.subscribeToErrors();

        ChatMessageRequest request = ChatMessageRequest.builder()
                .groupId(1L)
                .content("")
                .build();

        // when
        stompSession.send(webSocketTestSupport.getSendEndpoint(), request);

        // then
        ErrorResponse error = errorFuture.get(5, TimeUnit.SECONDS);
        assertThat(error.getCode()).isEqualTo(ErrorCode.INVALID_API.getCode());
    }

    @Test
    @DisplayName("메시지 전송 - 길이 초과")
    void sendMessage_ContentTooLong_Fail() throws Exception {
        // given
        CompletableFuture<ErrorResponse> errorFuture = webSocketTestSupport.subscribeToErrors();

        String longContent = "a".repeat(1001); // 1000자 제한
        ChatMessageRequest request = ChatMessageRequest.builder()
                .groupId(1L)
                .content(longContent)
                .build();

        // when
        stompSession.send(webSocketTestSupport.getSendEndpoint(), request);

        // then
        ErrorResponse error = errorFuture.get(5, TimeUnit.SECONDS);
        assertThat(error.getCode()).isEqualTo(ErrorCode.INVALID_API.getCode());
    }

    @Test
    @DisplayName("WebSocket 연결 - JWT 토큰 없음")
    void connect_NoJwtToken_Fail() {
        // given
        WebSocketTestSupport invalidWebSocketSupport = new WebSocketTestSupport(
                String.format("ws://localhost:%d/ws", port),
                objectMapper,
                null);

        // when & then
        assertThatThrownBy(invalidWebSocketSupport::connect)
                .isInstanceOf(ExecutionException.class)
                .hasRootCauseInstanceOf(ConnectionLostException.class);
    }

    @Test
    @DisplayName("WebSocket 연결 - 유효하지 않은 JWT 토큰")
    void connect_InvalidJwtToken_Fail() {
        // given
        WebSocketTestSupport invalidWebSocketSupport = new WebSocketTestSupport(
                String.format("ws://localhost:%d/ws", port),
                objectMapper,
                "invalid.jwt.token");

        // when & then
        assertThatThrownBy(invalidWebSocketSupport::connect)
                .isInstanceOf(ExecutionException.class)
                .hasRootCauseInstanceOf(ConnectionLostException.class);
    }
}

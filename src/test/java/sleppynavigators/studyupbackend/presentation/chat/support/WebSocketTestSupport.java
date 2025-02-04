package sleppynavigators.studyupbackend.presentation.chat.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import sleppynavigators.studyupbackend.presentation.common.SuccessResponse;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.awaitility.Awaitility.await;

public class WebSocketTestSupport {

    private static final String GROUP_DESTINATION_FORMAT = "/topic/group/%d";
    private static final String SEND_ENDPOINT = "/app/chat/message";
    private static final String USER_ERROR_DESTINATION = "/user/queue/errors";
    private static final String PUBLIC_ERROR_DESTINATION = "/topic/errors";
    private static final int DEFAULT_TIMEOUT_SECONDS = 10;

    private final WebSocketStompClient stompClient;
    private final String url;
    private StompSession stompSession;
    private final ObjectMapper objectMapper;

    public WebSocketTestSupport(String url, ObjectMapper objectMapper) {
        this.url = url;
        this.objectMapper = objectMapper;
        this.stompClient = createStompClient(objectMapper);
    }

    private WebSocketStompClient createStompClient(ObjectMapper objectMapper) {
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketTransport webSocketTransport = new WebSocketTransport(webSocketClient);
        List<Transport> transports = List.of(webSocketTransport);
        SockJsClient sockJsClient = new SockJsClient(transports);

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setObjectMapper(objectMapper);
        stompClient.setMessageConverter(messageConverter);

        return stompClient;
    }

    public void connect() throws ExecutionException, InterruptedException, TimeoutException {
        this.stompSession = stompClient
                .connectAsync(url, new DefaultStompSessionHandler())
                .get(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> stompSession.isConnected());
    }

    public StompSession getStompSession() {
        return stompSession;
    }

    public String getGroupDestination(Long groupId) {
        return String.format(GROUP_DESTINATION_FORMAT, groupId);
    }

    public String getSendEndpoint() {
        return SEND_ENDPOINT;
    }

    // TODO: 나중에 OATUH2 인증을 추가할 때 해당 테스트 코드 제거
    public CompletableFuture<SuccessResponse<?>> subscribeToErrors() {
        return subscribeAndReceive(PUBLIC_ERROR_DESTINATION, new ParameterizedTypeReference<>() {
        });
    }

    // TODO: 나중에 OATUH2 인증을 추가할 때 해당 테스트 코드 추가
    public CompletableFuture<SuccessResponse<?>> subscribeToUserErrors(String username) {
        String destination = USER_ERROR_DESTINATION.replace("user", username);
        return subscribeAndReceive(destination, new ParameterizedTypeReference<>() {
        });
    }

    public <T> CompletableFuture<T> subscribeAndReceive(String destination,
                                                        ParameterizedTypeReference<T> responseType) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();

        stompSession.subscribe(destination, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return responseType.getType();
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    T result = objectMapper.convertValue(payload, objectMapper.constructType(responseType.getType()));
                    completableFuture.complete(result);
                } catch (Exception e) {
                    completableFuture.completeExceptionally(e);
                }
            }
        });

        return completableFuture;
    }

    public <T> CompletableFuture<T> subscribeAndReceive(String destination, Class<T> responseType) {
        return subscribeAndReceive(destination, ParameterizedTypeReference.forType(responseType));
    }

    public void disconnect() {
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.disconnect();
        }
    }

    private static class DefaultStompSessionHandler extends StompSessionHandlerAdapter {

        @Override
        public void handleException(StompSession session, StompCommand command,
                                    StompHeaders headers, byte[] payload, Throwable exception) {
            throw new RuntimeException("WebSocket 연결 중 오류 발생", exception);
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            throw new RuntimeException("WebSocket 전송 중 오류 발생", exception);
        }
    }
} 

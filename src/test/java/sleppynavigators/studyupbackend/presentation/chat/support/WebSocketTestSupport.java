package sleppynavigators.studyupbackend.presentation.chat.support;

import static org.awaitility.Awaitility.await;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import sleppynavigators.studyupbackend.exception.ErrorResponse;

public class WebSocketTestSupport {

    private static final String GROUP_DESTINATION_FORMAT = "/topic/group/%d";
    private static final String SEND_ENDPOINT = "/app/chat/message";
    private static final String USER_ERROR_DESTINATION = "/user/queue/errors";
    private static final String PUBLIC_ERROR_DESTINATION = "/topic/errors";
    private static final int DEFAULT_TIMEOUT_SECONDS = 10;

    private final WebSocketStompClient stompClient;
    private final String url;
    private final String accessToken;

    private StompSession stompSession;
    private final ObjectMapper objectMapper;

    public WebSocketTestSupport(String url, ObjectMapper objectMapper, String accessToken) {
        this.url = url;
        this.objectMapper = objectMapper;
        this.accessToken = accessToken;
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

    public StompSession getStompSession() {
        return stompSession;
    }

    public void connect() throws ExecutionException, InterruptedException, TimeoutException {
        StompHeaders connectHeaders = new StompHeaders();
        if (accessToken != null) {
            connectHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        }

        this.stompSession = stompClient
                .connectAsync(url, new WebSocketHttpHeaders(), connectHeaders, new DefaultStompSessionHandler())
                .get(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> stompSession.isConnected());
    }

    public String getGroupDestination(Long groupId) {
        return String.format(GROUP_DESTINATION_FORMAT, groupId);
    }

    public String getSendEndpoint() {
        return SEND_ENDPOINT;
    }

    public CompletableFuture<ErrorResponse> subscribeToErrors() {
        if (accessToken != null) {
            return subscribeAndReceive(USER_ERROR_DESTINATION, new ParameterizedTypeReference<>() {
            });
        }
        return subscribeAndReceive(PUBLIC_ERROR_DESTINATION, new ParameterizedTypeReference<>() {
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

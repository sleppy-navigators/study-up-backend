package sleppynavigators.studyupbackend.presentation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.unit.DataSize;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final int HEARTBEAT_INTERVAL = (int) TimeUnit.SECONDS.toMillis(10);
    private static final int MESSAGE_SIZE_LIMIT = (int) DataSize.ofKilobytes(64).toBytes();
    private static final int SEND_TIME_LIMIT = (int) TimeUnit.SECONDS.toMillis(20);
    private static final int SEND_BUFFER_SIZE_LIMIT = (int) DataSize.ofKilobytes(512).toBytes();

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic")
                .setHeartbeatValue(new long[]{HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL});
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(MESSAGE_SIZE_LIMIT)
                   .setSendTimeLimit(SEND_TIME_LIMIT)
                   .setSendBufferSizeLimit(SEND_BUFFER_SIZE_LIMIT);
    }
} 
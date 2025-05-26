package sleppynavigators.studyupbackend.infrastructure.notification;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.notification.NotificationMessage;
import sleppynavigators.studyupbackend.exception.client.InitializeFailedException;
import sleppynavigators.studyupbackend.exception.client.UnsuccessfulResponseException;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmClient {

    private final FcmProperties fcmProperties;

    @PostConstruct
    public void initialize() {
        try {
            ClassPathResource resource = new ClassPathResource(fcmProperties.serviceAccountKeyPath());

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase application has been initialized");
            }
        } catch (IOException e) {
            throw new InitializeFailedException("Failed to initialize Firebase application", e);
        }
    }

    public String sendMessage(String token, String title, String body) {
        return sendMessage(token, title, body, null, null);
    }

    public String sendMessage(String token, String title, String body, URL imageUrl, Map<String, String> data) {
        Message.Builder messageBuilder = Message.builder()
                .setToken(token)
                .setNotification(
                        Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .setImage(imageUrl != null ? imageUrl.toString() : null)
                                .build()
                );

        if (MapUtils.isNotEmpty(data)) {
            messageBuilder.putAllData(data);
        }

        try {
            String response = FirebaseMessaging.getInstance().send(messageBuilder.build());
            log.info("Successfully sent message: {}", response);
            return response;
        } catch (Exception e) {
            throw new UnsuccessfulResponseException("Failed to send FCM message", e);
        }
    }

    public BatchResponse sendMulticast(NotificationMessage message, List<String> tokens) {
        MulticastMessage.Builder messageBuilder = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(
                        Notification.builder()
                                .setTitle(message.title())
                                .setBody(message.body())
                                .setImage(message.imageUrl() != null ? message.imageUrl().toString() : null)
                                .build()
                );

        if (MapUtils.isNotEmpty(message.data())) {
            messageBuilder.putAllData(message.data());
        }

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(messageBuilder.build());
            log.info("배치 FCM 메시지 전송 완료 - 성공: {}, 실패: {}", response.getSuccessCount(), response.getFailureCount());
            
            List<SendResponse> responses = response.getResponses();
            for (int i = 0; i < responses.size(); i++) {
                if (!responses.get(i).isSuccessful()) {
                    log.warn("FCM 전송 실패 - 토큰: {}, 오류: {}", tokens.get(i), responses.get(i).getException().getMessage());
                }
            }
            
            return response;
        } catch (Exception e) {
            throw new UnsuccessfulResponseException("Failed to send FCM multicast message", e);
        }
    }
}

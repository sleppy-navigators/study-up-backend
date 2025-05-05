package sleppynavigators.studyupbackend.infrastructure.notification;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.exception.client.InitializeFailedException;
import sleppynavigators.studyupbackend.exception.client.UnsuccessfulResponseException;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmClient {

    private final FcmProperties fcmProperties;

    @PostConstruct
    public void initialize() {
        try {
            ClassPathResource resource = new ClassPathResource(fcmProperties.getServiceAccountKeyPath());

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

    public String sendMessage(String token, String title, String body, String imageUrl, Map<String, String> data) {
        Message.Builder messageBuilder = Message.builder()
                .setToken(token)
                .setNotification(
                        Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .setImage(imageUrl)
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
}

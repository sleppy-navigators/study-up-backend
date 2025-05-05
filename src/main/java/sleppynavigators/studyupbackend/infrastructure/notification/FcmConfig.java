package sleppynavigators.studyupbackend.infrastructure.notification;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FcmConfig {

    private final FcmProperties fcmProperties;

    @PostConstruct
    public void validateProperties() {
        log.info("Validating FCM properties...");
        fcmProperties.validate();
        log.info("FCM properties validation successful. Project ID: {}", fcmProperties.getProjectId());
    }
}

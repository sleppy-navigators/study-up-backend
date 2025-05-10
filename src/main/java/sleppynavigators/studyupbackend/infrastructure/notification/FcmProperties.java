package sleppynavigators.studyupbackend.infrastructure.notification;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fcm")
public record FcmProperties(
        String serviceAccountKeyPath,
        String projectId
) {

    public FcmProperties {
        if (serviceAccountKeyPath.isBlank()) {
            throw new IllegalArgumentException("Service account key path must not be null or empty");
        }
        if (projectId.isBlank()) {
            throw new IllegalArgumentException("Project ID must not be null or empty");
        }
    }
}

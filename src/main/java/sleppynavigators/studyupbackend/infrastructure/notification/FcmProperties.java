package sleppynavigators.studyupbackend.infrastructure.notification;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "fcm")
@Data
public class FcmProperties {

    private String serviceAccountKeyPath;
    private String projectId;

    public void validate() {
        if (StringUtils.isBlank(serviceAccountKeyPath)) {
            throw new IllegalArgumentException("FCM service account key path must not be null or empty");
        }
        if (StringUtils.isBlank(projectId)) {
            throw new IllegalArgumentException("FCM project ID must not be null or empty");
        }
    }
}

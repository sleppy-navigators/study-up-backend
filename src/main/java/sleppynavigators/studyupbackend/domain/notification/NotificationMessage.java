package sleppynavigators.studyupbackend.domain.notification;

import java.net.URL;
import java.util.Map;

public record NotificationMessage(
        String title,
        String body,
        URL imageUrl,
        Map<String, String> data
) {
} 

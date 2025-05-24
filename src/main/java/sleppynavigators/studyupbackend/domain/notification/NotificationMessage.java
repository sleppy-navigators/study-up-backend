package sleppynavigators.studyupbackend.domain.notification;

import java.util.Map;

public record NotificationMessage(
        String title,
        String body,
        String imageUrl,
        Map<String, String> data
) {
} 

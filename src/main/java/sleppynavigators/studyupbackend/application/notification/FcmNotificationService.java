package sleppynavigators.studyupbackend.application.notification;

import jakarta.annotation.Nullable;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.notification.FcmToken;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.database.EntityNotFoundException;
import sleppynavigators.studyupbackend.infrastructure.notification.FcmClient;
import sleppynavigators.studyupbackend.infrastructure.notification.FcmTokenRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FcmNotificationService {

    private final FcmClient fcmClient;
    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;

    // TODO(@Jayon): 추후 이벤트 pub/sub 구조로 변경해야 함.
    @Transactional(readOnly = true)
    public List<String> sendTestNotification(
            Long userId,
            String title,
            String body,
            @Nullable String imageUrl,
            @Nullable Map<String, String> data
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        List<FcmToken> tokens = fcmTokenRepository.findAllByUser(user);
        if (tokens.isEmpty()) {
            throw new EntityNotFoundException("No registered devices found for the user. id: " + userId);
        }

        List<String> successMessageIds = new ArrayList<>();
        for (FcmToken token : tokens) {
            try {
                String messageId = fcmClient.sendMessage(token.getToken(), title, body, imageUrl, data);
                successMessageIds.add(messageId);
            } catch (Exception e) {
                log.warn("Failed to send notification to token: {}", token.getToken(), e);
            }
        }
        return successMessageIds;
    }
}

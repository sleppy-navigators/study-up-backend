package sleppynavigators.studyupbackend.application.notification;

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
import sleppynavigators.studyupbackend.presentation.notification.dto.request.TestNotificationRequest;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FcmNotificationService {

    private final FcmClient fcmClient;
    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;

    // TODO(@Jayon): 추후 이벤트 pub/sub 구조로 변경해야 함.
    public List<String> sendTestNotification(Long userId, TestNotificationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        List<FcmToken> tokens = fcmTokenRepository.findAllByUserId(user.getId());
        if (tokens.isEmpty()) {
            throw new EntityNotFoundException("No registered devices found for the user. id: " + user.getId());
        }

        List<String> successMessageIds = new ArrayList<>();
        for (FcmToken token : tokens) {
            try {
                String messageId = fcmClient.sendMessage(token.getToken(), request.title(), request.body(), request.imageUrl(), request.data());
                successMessageIds.add(messageId);
            } catch (Exception e) {
                log.error("Failed to send notification to token: {}", token.getToken(), e);
            }
        }
        return successMessageIds;
    }
}

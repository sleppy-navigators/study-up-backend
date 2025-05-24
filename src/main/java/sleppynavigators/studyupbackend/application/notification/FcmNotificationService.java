package sleppynavigators.studyupbackend.application.notification;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.group.GroupMember;
import sleppynavigators.studyupbackend.domain.notification.FcmToken;
import sleppynavigators.studyupbackend.domain.notification.NotificationMessage;
import sleppynavigators.studyupbackend.domain.notification.NotificationMessageGenerator;
import sleppynavigators.studyupbackend.domain.notification.NotificationMessageGeneratorFactory;
import sleppynavigators.studyupbackend.domain.event.GroupNotificationEvent;
import sleppynavigators.studyupbackend.domain.event.NotificationEvent;
import sleppynavigators.studyupbackend.domain.event.PersonalNotificationEvent;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.database.EntityNotFoundException;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.notification.FcmClient;
import sleppynavigators.studyupbackend.infrastructure.notification.FcmTokenRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.notification.dto.request.TestNotificationRequest;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmNotificationService {

    private final NotificationMessageGeneratorFactory notificationMessageGeneratorFactory;
    private final FcmClient fcmClient;
    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public <T extends NotificationEvent> void sendNotification(T event) {
        NotificationMessageGenerator<T> notificationMessageGenerator = notificationMessageGeneratorFactory.get(event);
        NotificationMessage message = notificationMessageGenerator.generate(event);

        if (event instanceof GroupNotificationEvent groupEvent) {
            sendGroupNotification(groupEvent, message);
        } else if (event instanceof PersonalNotificationEvent personalEvent) {
            sendPersonalNotification(personalEvent, message);
        }
    }

    private void sendGroupNotification(GroupNotificationEvent event, NotificationMessage message) {
        Long groupId = event.getGroupId();
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found - groupId: " + groupId));

        List<User> members = group.getMembers().stream()
                .map(GroupMember::getUser)
                .toList();

        for (User member : members) {
            sendNotificationToUser(member.getId(), message);
        }
    }

    private void sendPersonalNotification(PersonalNotificationEvent event, NotificationMessage message) {
        sendNotificationToUser(event.getUserId(), message);
    }

    private void sendNotificationToUser(Long userId, NotificationMessage message) {
        List<FcmToken> tokens = fcmTokenRepository.findAllByUserId(userId);
        if (tokens.isEmpty()) {
            log.warn("No registered devices found for the user. id: {}", userId);
            return;
        }

        for (FcmToken token : tokens) {
            try {
                URL imageUrl = message.imageUrl() != null ? new URL(message.imageUrl()) : null;
                String messageId = fcmClient.sendMessage(
                    token.getToken(), 
                    message.title(), 
                    message.body(), 
                    imageUrl, 
                    message.data()
                );
                log.debug("FCM 알림 전송 성공 - userId: {}, messageId: {}", userId, messageId);
            } catch (Exception e) {
                log.error("Failed to send notification to token: {} for user: {}", token.getToken(), userId, e);
            }
        }
    }

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

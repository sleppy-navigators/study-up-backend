package sleppynavigators.studyupbackend.application.event;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import sleppynavigators.studyupbackend.application.notification.FcmNotificationService;
import sleppynavigators.studyupbackend.domain.event.NotificationEvent;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationEventListener {

    private final FcmNotificationService fcmNotificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    // NOTE: Transaction in this method cannot be committed
    public void handleNotificationEvent(NotificationEvent event) {
        // fcmNotificationService.sendNotification(event);
    }
}

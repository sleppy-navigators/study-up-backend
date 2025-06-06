package sleppynavigators.studyupbackend.domain.notification.generator;

import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.NotificationEvent;
import sleppynavigators.studyupbackend.domain.notification.NotificationMessage;

// TODO(@Jayon): 시스템 메시지 / FCM 푸시 알림 메시지를 정의하다보니, 두 성격이 굉장히 비슷하다고 느낌. 추후 통합화하는 작업 진행.
public interface NotificationMessageGenerator<T extends NotificationEvent> {
    NotificationMessage generate(T event);

    EventType supportedEventType();
}

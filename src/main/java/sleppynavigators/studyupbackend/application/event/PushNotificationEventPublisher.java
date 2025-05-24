package sleppynavigators.studyupbackend.application.event;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.event.PushNotificationEvent;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PushNotificationEventPublisher {

  private final ApplicationEventPublisher publisher;

  public void publish(PushNotificationEvent event) {
    publisher.publishEvent(event);
  }

}

package sleppynavigators.studyupbackend.application.event;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.event.NotificationEvent;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationEventPublisher {

  private final ApplicationEventPublisher publisher;

  public void publish(NotificationEvent event) {
    publisher.publishEvent(event);
  }

}

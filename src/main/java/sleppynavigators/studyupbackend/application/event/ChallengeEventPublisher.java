package sleppynavigators.studyupbackend.application.event;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.event.ChallengeCompleteEvent;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeEventPublisher {

  private final ApplicationEventPublisher publisher;

  public void publishChallengeCompleteEvent(ChallengeCompleteEvent event) {
    publisher.publishEvent(event);
  }
}

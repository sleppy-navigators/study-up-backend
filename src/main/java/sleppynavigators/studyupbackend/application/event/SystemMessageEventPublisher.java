package sleppynavigators.studyupbackend.application.event;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.event.SystemMessageEvent;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SystemMessageEventPublisher {
    private final ApplicationEventPublisher publisher;

    public void publish(SystemMessageEvent event) {
        publisher.publishEvent(event);
    }
}

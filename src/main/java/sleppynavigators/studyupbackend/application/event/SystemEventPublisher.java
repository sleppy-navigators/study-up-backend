package sleppynavigators.studyupbackend.application.event;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.event.SystemEvent;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SystemEventPublisher {
    private final ApplicationEventPublisher publisher;

    public void publish(SystemEvent event) {
        publisher.publishEvent(event);
    }
}

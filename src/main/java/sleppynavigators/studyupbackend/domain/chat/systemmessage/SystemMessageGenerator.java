package sleppynavigators.studyupbackend.domain.chat.systemmessage;

import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.SystemEvent;

public interface SystemMessageGenerator<T extends SystemEvent> {
    String generate(T event);

    EventType supportedEventType();
}

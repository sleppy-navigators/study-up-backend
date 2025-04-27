package sleppynavigators.studyupbackend.domain.chat.systemmessage;

import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.SystemMessageEvent;

public interface SystemMessageGenerator<T extends SystemMessageEvent> {
    String generate(T event);

    EventType getEventType();
}

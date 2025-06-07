package sleppynavigators.studyupbackend.domain.chat.generator.action;

import java.util.List;
import sleppynavigators.studyupbackend.domain.chat.action.ChatAction;
import sleppynavigators.studyupbackend.domain.event.Event;
import sleppynavigators.studyupbackend.domain.event.EventType;

public interface ChatActionListGenerator<T extends Event> {

    List<ChatAction> generate(T event);

    EventType supportedEventType();
}

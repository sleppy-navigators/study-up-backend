package sleppynavigators.studyupbackend.domain.chat.generator.action;

import java.util.List;
import sleppynavigators.studyupbackend.domain.chat.action.ChatAction;
import sleppynavigators.studyupbackend.domain.event.Event;
import sleppynavigators.studyupbackend.domain.event.EventType;

public class FallbackChatActionListGenerator implements ChatActionListGenerator<Event> {

    @Override
    public List<ChatAction> generate(Event event) {
        return List.of();
    }

    @Override
    public EventType supportedEventType() {
        throw new UnsupportedOperationException("Fallback generator does not support any specific event type");
    }
}

package sleppynavigators.studyupbackend.domain.chat.generator.action;

import java.util.List;
import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.chat.action.ChatAction;
import sleppynavigators.studyupbackend.domain.chat.action.ViewMemberProfileChatAction;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.UserJoinEvent;

@Component
public class UserJoinChatActionListGenerator implements ChatActionListGenerator<UserJoinEvent> {

    @Override
    public List<ChatAction> generate(UserJoinEvent event) {
        return List.of(new ViewMemberProfileChatAction(event.userId()));
    }

    @Override
    public EventType supportedEventType() {
        return EventType.USER_JOIN;
    }
}

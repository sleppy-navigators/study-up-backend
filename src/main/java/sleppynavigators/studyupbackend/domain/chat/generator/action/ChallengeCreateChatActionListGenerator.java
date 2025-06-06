package sleppynavigators.studyupbackend.domain.chat.generator.action;

import java.util.List;
import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.chat.action.ChatAction;
import sleppynavigators.studyupbackend.domain.chat.action.ViewChallengeDetailChatAction;
import sleppynavigators.studyupbackend.domain.event.challenge.ChallengeCreateEvent;
import sleppynavigators.studyupbackend.domain.event.EventType;

@Component
public class ChallengeCreateChatActionListGenerator implements ChatActionListGenerator<ChallengeCreateEvent> {

    @Override
    public List<ChatAction> generate(ChallengeCreateEvent event) {
        return List.of(
                new ViewChallengeDetailChatAction(event.challengeId()));
    }

    @Override
    public EventType supportedEventType() {
        return EventType.CHALLENGE_CREATE;
    }
}

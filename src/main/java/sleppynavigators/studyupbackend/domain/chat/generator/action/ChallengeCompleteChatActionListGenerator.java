package sleppynavigators.studyupbackend.domain.chat.generator.action;

import java.util.List;
import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.chat.action.ChatAction;
import sleppynavigators.studyupbackend.domain.chat.action.ShowOfChallengeChatAction;
import sleppynavigators.studyupbackend.domain.chat.action.ViewChallengeDetailChatAction;
import sleppynavigators.studyupbackend.domain.event.challenge.ChallengeCompleteEvent;
import sleppynavigators.studyupbackend.domain.event.EventType;

@Component
public class ChallengeCompleteChatActionListGenerator implements ChatActionListGenerator<ChallengeCompleteEvent> {

    @Override
    public List<ChatAction> generate(ChallengeCompleteEvent event) {
        return List.of(
                new ShowOfChallengeChatAction(event.challengeId()),
                new ViewChallengeDetailChatAction(event.challengeId()));
    }

    @Override
    public EventType supportedEventType() {
        return EventType.CHALLENGE_COMPLETE;
    }
}

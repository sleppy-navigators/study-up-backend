package sleppynavigators.studyupbackend.domain.chat.generator.action;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import sleppynavigators.studyupbackend.domain.chat.action.ChatAction;
import sleppynavigators.studyupbackend.domain.chat.action.ChatActionType;
import sleppynavigators.studyupbackend.domain.event.challenge.ChallengeCreateEvent;
import sleppynavigators.studyupbackend.domain.event.EventType;

public class ChallengeCreateChatActionListGeneratorTest {

    private final ChallengeCreateChatActionListGenerator generator = new ChallengeCreateChatActionListGenerator();

    @Test
    void getEventType_ShouldReturnChallengeCreate() {
        // when
        EventType eventType = generator.supportedEventType();

        // then
        assertThat(eventType).isEqualTo(EventType.CHALLENGE_CREATE);
    }

    @Test
    void generate_ShouldReturnChatActions() {
        // given
        String userName = "홍길동";
        String challengeName = "알고리즘 문제 풀기";
        Long groupId = 1L;
        Long challengeId = 1L;
        ChallengeCreateEvent event = new ChallengeCreateEvent(userName, challengeName, groupId, challengeId);

        // when
        List<ChatAction> chatActions = generator.generate(event);

        // then
        assertThat(chatActions).hasSize(1);
        assertThat(chatActions.get(0).getType()).isEqualTo(ChatActionType.VIEW_CHALLENGE_DETAIL);
    }
}

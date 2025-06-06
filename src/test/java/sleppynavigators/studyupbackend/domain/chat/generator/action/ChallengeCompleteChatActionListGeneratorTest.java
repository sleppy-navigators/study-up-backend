package sleppynavigators.studyupbackend.domain.chat.generator.action;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import sleppynavigators.studyupbackend.domain.chat.action.ChatAction;
import sleppynavigators.studyupbackend.domain.chat.action.ChatActionType;
import sleppynavigators.studyupbackend.domain.event.challenge.ChallengeCompleteEvent;
import sleppynavigators.studyupbackend.domain.event.EventType;

public class ChallengeCompleteChatActionListGeneratorTest {

    private final ChallengeCompleteChatActionListGenerator generator = new ChallengeCompleteChatActionListGenerator();

    @Test
    void getEventType_ShouldReturnChallengeComplete() {
        // when
        EventType eventType = generator.supportedEventType();

        // then
        assertThat(eventType).isEqualTo(EventType.CHALLENGE_COMPLETE);
    }

    @Test
    void generate_ShouldReturnChatActions() {
        // given
        String userName = "홍길동";
        String challengeName = "알고리즘 문제 풀기";
        Long groupId = 1L;
        Long challengeId = 1L;
        Long challengerId = 1L;
        ChallengeCompleteEvent event =
                new ChallengeCompleteEvent(userName, challengeName, groupId, challengeId, challengerId, 98.857);

        // when
        List<ChatAction> chatActions = generator.generate(event);

        // then
        assertThat(chatActions).hasSize(2);
        assertThat(chatActions.get(0).getType()).isEqualTo(ChatActionType.SHOW_OF_CHALLENGE);
        assertThat(chatActions.get(1).getType()).isEqualTo(ChatActionType.VIEW_CHALLENGE_DETAIL);
    }
}

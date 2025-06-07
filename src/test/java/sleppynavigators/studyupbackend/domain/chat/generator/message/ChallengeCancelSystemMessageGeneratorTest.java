package sleppynavigators.studyupbackend.domain.chat.generator.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sleppynavigators.studyupbackend.domain.event.challenge.ChallengeCancelEvent;
import sleppynavigators.studyupbackend.domain.event.EventType;

class ChallengeCancelSystemMessageGeneratorTest {

    private final ChallengeCancelSystemMessageGenerator generator = new ChallengeCancelSystemMessageGenerator();

    @Test
    void getEventType_ShouldReturnChallengeCancel() {
        // when
        EventType eventType = generator.supportedEventType();

        // then
        assertThat(eventType).isEqualTo(EventType.CHALLENGE_CANCEL);
    }

    @Test
    void generate_ShouldReturnFormattedMessage() {
        // given
        String userName = "홍길동";
        String challengeName = "알고리즘 문제 풀기";
        Long groupId = 1L;
        ChallengeCancelEvent event = new ChallengeCancelEvent(userName, challengeName, groupId);

        // when
        String message = generator.generate(event);

        // then
        assertThat(message).isEqualTo("홍길동님이 '알고리즘 문제 풀기' 챌린지를 취소했습니다.");
    }
}

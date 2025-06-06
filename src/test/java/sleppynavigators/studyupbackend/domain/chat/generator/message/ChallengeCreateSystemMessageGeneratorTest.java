package sleppynavigators.studyupbackend.domain.chat.generator.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sleppynavigators.studyupbackend.domain.event.challenge.ChallengeCreateEvent;
import sleppynavigators.studyupbackend.domain.event.EventType;

class ChallengeCreateSystemMessageGeneratorTest {

    private final ChallengeCreateSystemMessageGenerator generator = new ChallengeCreateSystemMessageGenerator();

    @Test
    void getEventType_ShouldReturnChallengeCreate() {
        // when
        EventType eventType = generator.supportedEventType();

        // then
        assertThat(eventType).isEqualTo(EventType.CHALLENGE_CREATE);
    }

    @Test
    void generate_ShouldReturnFormattedMessage() {
        // given
        String userName = "홍길동";
        String challengeName = "알고리즘 문제 풀기";
        Long groupId = 1L;
        Long challengeId = 1L;
        ChallengeCreateEvent event = new ChallengeCreateEvent(userName, challengeName, groupId, challengeId);

        // when
        String message = generator.generate(event);

        // then
        assertThat(message).isEqualTo("홍길동님이 '알고리즘 문제 풀기' 챌린지를 생성했습니다.");
    }
}

package sleppynavigators.studyupbackend.domain.chat.generator.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sleppynavigators.studyupbackend.domain.event.challenge.ChallengeCompleteEvent;
import sleppynavigators.studyupbackend.domain.event.EventType;

class ChallengeCompleteSystemMessageGeneratorTest {

    private final ChallengeCompleteSystemMessageGenerator generator = new ChallengeCompleteSystemMessageGenerator();

    @Test
    void getEventType_ShouldReturnChallengeComplete() {
        // when
        EventType eventType = generator.supportedEventType();

        // then
        assertThat(eventType).isEqualTo(EventType.CHALLENGE_COMPLETE);
    }

    @Test
    void generate_ShouldReturnFormattedMessage() {
        // given
        String userName = "홍길동";
        String challengeName = "알고리즘 문제 풀기";
        Long groupId = 1L;
        Long challengeId = 1L;
        Long challengerId = 1L;
        ChallengeCompleteEvent event =
                new ChallengeCompleteEvent(userName, challengeName, groupId, challengeId, challengerId, 98.857);

        // when
        String message = generator.generate(event);

        // then
        assertThat(message).isEqualTo("홍길동님이 '알고리즘 문제 풀기' 챌린지를 완료했습니다. (98.86% 달성)");
    }
}

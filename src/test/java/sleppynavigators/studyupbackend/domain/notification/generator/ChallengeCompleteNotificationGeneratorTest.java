package sleppynavigators.studyupbackend.domain.notification.generator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.challenge.ChallengeCompleteEvent;
import sleppynavigators.studyupbackend.domain.notification.NotificationMessage;

public class ChallengeCompleteNotificationGeneratorTest {

    private final ChallengeCompleteNotificationMessageGenerator generator = new ChallengeCompleteNotificationMessageGenerator();

    @Test
    void getEventType_ShouldReturnChallengeComplete() {
        // when
        EventType eventType = generator.supportedEventType();

        // then
        assertThat(eventType).isEqualTo(EventType.CHALLENGE_COMPLETE);
    }

    @Test
    void generate_ShouldReturnNotificationMessage() {
        // given
        String userName = "홍길동";
        String challengeName = "알고리즘 문제 풀기";
        Long groupId = 1L;
        Long challengeId = 1L;
        Long challengerId = 1L;
        ChallengeCompleteEvent event =
                new ChallengeCompleteEvent(userName, challengeName, groupId, challengeId, challengerId, 98.857);

        // when
        NotificationMessage message = generator.generate(event);

        // then
        assertThat(message.title()).isEqualTo("챌린지 완료 알림");
        assertThat(message.body()).isEqualTo("홍길동님이 '알고리즘 문제 풀기' 챌린지를 완료했습니다. (98.86% 달성)");
    }
}

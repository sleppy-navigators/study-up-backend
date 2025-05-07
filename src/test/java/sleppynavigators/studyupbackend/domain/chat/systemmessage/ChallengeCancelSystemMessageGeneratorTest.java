package sleppynavigators.studyupbackend.domain.chat.systemmessage;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sleppynavigators.studyupbackend.domain.event.ChallengeCancelEvent;
import sleppynavigators.studyupbackend.domain.event.EventType;

@DisplayName("[도메인] ChallengeCancelSystemMessageGenerator 테스트")
class ChallengeCancelSystemMessageGeneratorTest {

    private final ChallengeCancelSystemMessageGenerator generator = new ChallengeCancelSystemMessageGenerator();

    @Test
    @DisplayName("지원하는 이벤트 타입 조회 - 성공")
    void getEventType_ShouldReturnChallengeCancel() {
        // when
        EventType eventType = generator.supportedEventType();

        // then
        assertThat(eventType).isEqualTo(EventType.CHALLENGE_CANCEL);
    }

    @Test
    @DisplayName("시스템 메시지 생성 - 성공")
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

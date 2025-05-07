package sleppynavigators.studyupbackend.domain.chat.systemmessage;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.UserJoinEvent;

@DisplayName("[도메인] UserJoinSystemMessageGenerator 테스트")
class UserJoinSystemMessageGeneratorTest {

    private final UserJoinSystemMessageGenerator generator = new UserJoinSystemMessageGenerator();

    @Test
    @DisplayName("지원하는 이벤트 타입 조회 - 성공")
    void getEventType_ShouldReturnUserJoin() {
        // when
        EventType eventType = generator.supportedEventType();

        // then
        assertThat(eventType).isEqualTo(EventType.USER_JOIN);
    }

    @Test
    @DisplayName("시스템 메시지 생성 - 성공")
    void generate_ShouldReturnFormattedMessage() {
        // given
        String userName = "홍길동";
        Long groupId = 1L;
        UserJoinEvent event = new UserJoinEvent(userName, groupId);

        // when
        String message = generator.generate(event);

        // then
        assertThat(message).isEqualTo("홍길동님이 그룹에 참여했습니다.");
    }
}

package sleppynavigators.studyupbackend.domain.chat.systemmessage;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.GroupCreateEvent;

@DisplayName("[도메인] GroupCreateSystemMessageGenerator 테스트")
class GroupCreateSystemMessageGeneratorTest {

    private final GroupCreateSystemMessageGenerator generator = new GroupCreateSystemMessageGenerator();

    @Test
    @DisplayName("지원하는 이벤트 타입 조회 - 성공")
    void getEventType_ShouldReturnGroupCreate() {
        // when
        EventType eventType = generator.supportedEventType();

        // then
        assertThat(eventType).isEqualTo(EventType.GROUP_CREATE);
    }

    @Test
    @DisplayName("시스템 메시지 생성 - 성공")
    void generate_ShouldReturnFormattedMessage() {
        // given
        String userName = "홍길동";
        String groupName = "알고리즘 스터디";
        Long groupId = 1L;
        GroupCreateEvent event = new GroupCreateEvent(userName, groupName, groupId);

        // when
        String message = generator.generate(event);

        // then
        assertThat(message).isEqualTo("홍길동님이 '알고리즘 스터디' 그룹을 생성했습니다.");
    }
}

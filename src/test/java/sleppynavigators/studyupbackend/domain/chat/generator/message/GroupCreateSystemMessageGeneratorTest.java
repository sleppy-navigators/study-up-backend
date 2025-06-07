package sleppynavigators.studyupbackend.domain.chat.generator.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.group.GroupCreateEvent;

class GroupCreateSystemMessageGeneratorTest {

    private final GroupCreateSystemMessageGenerator generator = new GroupCreateSystemMessageGenerator();

    @Test
    void getEventType_ShouldReturnGroupCreate() {
        // when
        EventType eventType = generator.supportedEventType();

        // then
        assertThat(eventType).isEqualTo(EventType.GROUP_CREATE);
    }

    @Test
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

package sleppynavigators.studyupbackend.domain.chat.systemmessage;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.TaskCertifyEvent;

public class TaskCertifySystemMessageGeneratorTest {

    private final TaskCertifySystemMessageGenerator generator = new TaskCertifySystemMessageGenerator();

    @Test
    void getEventType_ShouldReturnTaskCertify() {
        // when
        EventType eventType = generator.supportedEventType();

        // then
        assertThat(eventType).isEqualTo(EventType.TASK_CERTIFY);
    }

    @Test
    void generate_ShouldReturnFormattedMessage() {
        // given
        String userName = "홍길동";
        String challengeName = "알고리즘 끝장내기";
        String taskName = "알고리즘 문제 풀기";
        Long groupId = 1L;
        Long taskId = 1L;
        TaskCertifyEvent event = new TaskCertifyEvent(userName, challengeName, taskName, groupId, taskId);

        // when
        String message = generator.generate(event);

        // then
        assertThat(message).isEqualTo("홍길동님이 '알고리즘 문제 풀기' 테스크를 완료했습니다. (알고리즘 끝장내기)");
    }
}

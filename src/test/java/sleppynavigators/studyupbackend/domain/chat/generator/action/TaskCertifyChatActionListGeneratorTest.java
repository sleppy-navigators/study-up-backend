package sleppynavigators.studyupbackend.domain.chat.generator.action;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import sleppynavigators.studyupbackend.domain.chat.action.ChatAction;
import sleppynavigators.studyupbackend.domain.chat.action.ChatActionType;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.challenge.TaskCertifyEvent;

public class TaskCertifyChatActionListGeneratorTest {

    private final TaskCertifyChatActionListGenerator generator = new TaskCertifyChatActionListGenerator();

    @Test
    void getEventType_ShouldReturnTaskCertify() {
        // when
        EventType eventType = generator.supportedEventType();

        // then
        assertThat(eventType).isEqualTo(EventType.TASK_CERTIFY);
    }

    @Test
    void generate_ShouldReturnChatActions() {
        // given
        String userName = "홍길동";
        String challengeName = "알고리즘 끝장내기";
        String taskName = "알고리즘 문제 풀기";
        Long groupId = 1L;
        Long challengeId = 1L;
        Long taskId = 1L;
        TaskCertifyEvent event = new TaskCertifyEvent(userName, challengeName, taskName, groupId, challengeId, taskId);

        // when
        List<ChatAction> chatActions = generator.generate(event);

        // then
        assertThat(chatActions).hasSize(1);
        assertThat(chatActions.get(0).getType()).isEqualTo(ChatActionType.VIEW_TASK_DETAIL);
    }
}

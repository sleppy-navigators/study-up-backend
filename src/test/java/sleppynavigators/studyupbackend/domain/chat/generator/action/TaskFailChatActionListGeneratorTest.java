package sleppynavigators.studyupbackend.domain.chat.generator.action;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import sleppynavigators.studyupbackend.domain.chat.action.ChatAction;
import sleppynavigators.studyupbackend.domain.chat.action.ChatActionType;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.TaskFailEvent;

public class TaskFailChatActionListGeneratorTest {

    private final TaskFailChatActionListGenerator generator = new TaskFailChatActionListGenerator();

    @Test
    void getEventType_ShouldReturnTaskFail() {
        // when
        EventType eventType = generator.supportedEventType();

        // then
        assertThat(eventType).isEqualTo(EventType.TASK_FAIL);
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
        TaskFailEvent event = new TaskFailEvent(userName, challengeName, taskName, groupId, challengeId, taskId);

        // when
        List<ChatAction> chatActions = generator.generate(event);

        // then
        assertThat(chatActions).hasSize(2);
        assertThat(chatActions.get(0).getType()).isEqualTo(ChatActionType.HUNT_TASK);
        assertThat(chatActions.get(1).getType()).isEqualTo(ChatActionType.VIEW_TASK_DETAIL);
    }
}

package sleppynavigators.studyupbackend.domain.chat.generator.action;

import java.util.List;
import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.chat.action.ChatAction;
import sleppynavigators.studyupbackend.domain.chat.action.HuntTaskChatAction;
import sleppynavigators.studyupbackend.domain.chat.action.ViewTaskDetailChatAction;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.challenge.TaskFailEvent;

@Component
public class TaskFailChatActionListGenerator implements ChatActionListGenerator<TaskFailEvent> {

    @Override
    public List<ChatAction> generate(TaskFailEvent event) {
        return List.of(new HuntTaskChatAction(event.challengeId(), event.taskId()),
                new ViewTaskDetailChatAction(event.challengeId(), event.taskId()));
    }

    @Override
    public EventType supportedEventType() {
        return EventType.TASK_FAIL;
    }
}

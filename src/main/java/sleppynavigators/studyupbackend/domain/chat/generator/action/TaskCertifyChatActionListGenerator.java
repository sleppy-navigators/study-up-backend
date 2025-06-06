package sleppynavigators.studyupbackend.domain.chat.generator.action;

import java.util.List;
import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.chat.action.ChatAction;
import sleppynavigators.studyupbackend.domain.chat.action.ViewTaskDetailChatAction;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.challenge.TaskCertifyEvent;

@Component
public class TaskCertifyChatActionListGenerator implements ChatActionListGenerator<TaskCertifyEvent> {

    @Override
    public List<ChatAction> generate(TaskCertifyEvent event) {
        return List.of(new ViewTaskDetailChatAction(event.challengeId(), event.taskId()));
    }

    @Override
    public EventType supportedEventType() {
        return EventType.TASK_CERTIFY;
    }
}

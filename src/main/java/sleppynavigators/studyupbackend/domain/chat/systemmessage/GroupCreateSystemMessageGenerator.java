package sleppynavigators.studyupbackend.domain.chat.systemmessage;

import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.GroupCreateEvent;

@Component
public class GroupCreateSystemMessageGenerator implements SystemMessageGenerator<GroupCreateEvent> {
    private static final String MESSAGE_FORMAT = "%s님이 '%s' 그룹을 생성했습니다.";
    
    @Override
    public String generate(GroupCreateEvent event) {
        return String.format(MESSAGE_FORMAT, event.userName(), event.groupName());
    }

    @Override
    public EventType getEventType() {
        return EventType.GROUP_CREATE;
    }
}

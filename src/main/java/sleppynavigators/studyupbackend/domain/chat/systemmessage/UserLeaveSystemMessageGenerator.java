package sleppynavigators.studyupbackend.domain.chat.systemmessage;

import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.UserLeaveEvent;

@Component
public class UserLeaveSystemMessageGenerator implements SystemMessageGenerator<UserLeaveEvent> {
    private static final String MESSAGE_FORMAT = "%s님이 그룹을 나갔습니다.";
    
    @Override
    public String generate(UserLeaveEvent event) {
        return String.format(MESSAGE_FORMAT, event.userName());
    }

    @Override
    public EventType getEventType() {
        return EventType.USER_LEAVE;
    }
}

package sleppynavigators.studyupbackend.domain.message;

import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.UserLeaveEvent;

public class UserLeaveSystemMessageGenerator implements SystemMessageGenerator<UserLeaveEvent> {
    private static final String MESSAGE_FORMAT = "%s님이 그룹을 나갔습니다.";
    
    @Override
    public String generate(UserLeaveEvent event) {
        return String.format(MESSAGE_FORMAT, event.userName());
    }
    
    @Override
    public boolean supports(UserLeaveEvent event) {
        return event.getType() == EventType.USER_LEAVE;
    }
}

package sleppynavigators.studyupbackend.domain.event;

import sleppynavigators.studyupbackend.domain.chat.SystemMessageTemplate;

public record UserLeaveEvent(String userName, String reason, String leaveTime) implements SystemEvent {

    @Override
    public SystemEventType getType() {
        return SystemEventType.USER_LEAVE;
    }

    @Override
    public String generateMessage(SystemMessageTemplate template) {
        return template.format(userName);
    }
}

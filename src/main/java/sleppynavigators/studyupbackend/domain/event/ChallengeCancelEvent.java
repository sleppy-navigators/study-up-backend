package sleppynavigators.studyupbackend.domain.event;

import sleppynavigators.studyupbackend.domain.chat.SystemMessageTemplate;

public record ChallengeCancelEvent(String userName, String challengeName, Long groupId) implements SystemEvent {

    @Override
    public SystemEventType getType() {
        return SystemEventType.CHALLENGE_CANCEL;
    }

    @Override
    public Long getGroupId() {
        return groupId;
    }

    @Override
    public String generateMessage(SystemMessageTemplate template) {
        return template.format(userName, challengeName);
    }
}

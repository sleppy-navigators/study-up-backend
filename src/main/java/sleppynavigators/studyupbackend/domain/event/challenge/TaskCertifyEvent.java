package sleppynavigators.studyupbackend.domain.event.challenge;

import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.GroupNotificationEvent;
import sleppynavigators.studyupbackend.domain.event.SystemMessageEvent;

public record TaskCertifyEvent(
        String userName, String challengeName, String taskName, Long groupId, Long challengeId, Long taskId
) implements SystemMessageEvent, GroupNotificationEvent {

    @Override
    public EventType getType() {
        return EventType.TASK_CERTIFY;
    }

    @Override
    public Long getGroupId() {
        return groupId;
    }
}

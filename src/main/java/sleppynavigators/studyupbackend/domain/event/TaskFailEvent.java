package sleppynavigators.studyupbackend.domain.event;

public record TaskFailEvent(
        String userName, String challengeName, Long groupId, Long challengeId, Long challengerId)
        implements SystemMessageEvent, GroupNotificationEvent {

    @Override
    public EventType getType() {
        return EventType.TASK_FAIL;
    }

    @Override
    public Long getGroupId() {
        return groupId;
    }
}

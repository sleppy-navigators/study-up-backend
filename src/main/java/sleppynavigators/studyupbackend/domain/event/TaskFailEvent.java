package sleppynavigators.studyupbackend.domain.event;

public record TaskFailEvent(
        String userName, String challengeName, String taskName, Long groupId)
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

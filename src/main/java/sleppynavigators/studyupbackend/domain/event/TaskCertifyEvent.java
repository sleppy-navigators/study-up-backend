package sleppynavigators.studyupbackend.domain.event;

public record TaskCertifyEvent(
        String userName, String challengeName, String taskName, Long groupId
) implements SystemMessageEvent {

    @Override
    public EventType getType() {
        return EventType.TASK_CERTIFY;
    }

    @Override
    public Long getGroupId() {
        return groupId;
    }
}

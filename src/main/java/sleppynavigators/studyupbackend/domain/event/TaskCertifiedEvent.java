package sleppynavigators.studyupbackend.domain.event;

public record TaskCertifiedEvent(
        String userName, String challengeName, String taskName, Long groupId
) implements SystemEvent {

    @Override
    public EventType getType() {
        return EventType.TASK_CERTIFY;
    }

    @Override
    public Long getGroupId() {
        return groupId;
    }
}

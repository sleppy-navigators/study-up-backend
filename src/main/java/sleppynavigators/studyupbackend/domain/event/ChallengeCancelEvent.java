package sleppynavigators.studyupbackend.domain.event;

public record ChallengeCancelEvent(String userName, String challengeName, Long groupId) implements SystemMessageEvent {
    @Override
    public EventType getType() {
        return EventType.CHALLENGE_CANCEL;
    }

    @Override
    public Long getGroupId() {
        return groupId;
    }
}

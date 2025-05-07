package sleppynavigators.studyupbackend.domain.event;

public record ChallengeCompleteEvent(
        String userName, String challengeName, Long groupId, Double percentage)
        implements SystemEvent {

    @Override
    public EventType getType() {
        return EventType.CHALLENGE_COMPLETE;
    }

    @Override
    public Long getGroupId() {
        return groupId;
    }
}

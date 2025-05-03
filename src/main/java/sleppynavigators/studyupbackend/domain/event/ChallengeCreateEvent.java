package sleppynavigators.studyupbackend.domain.event;

public record ChallengeCreateEvent(String userName, String challengeName, Long groupId) implements SystemEvent {

    @Override
    public EventType getType() {
        return EventType.CHALLENGE_CREATE;
    }

    @Override
    public Long getGroupId() {
        return groupId;
    }
}

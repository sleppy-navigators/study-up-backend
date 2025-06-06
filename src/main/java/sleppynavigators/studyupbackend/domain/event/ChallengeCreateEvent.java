package sleppynavigators.studyupbackend.domain.event;

public record ChallengeCreateEvent(String userName, String challengeName, Long groupId, Long challengeId)
        implements SystemMessageEvent {

    @Override
    public EventType getType() {
        return EventType.CHALLENGE_CREATE;
    }

    @Override
    public Long getGroupId() {
        return groupId;
    }
}

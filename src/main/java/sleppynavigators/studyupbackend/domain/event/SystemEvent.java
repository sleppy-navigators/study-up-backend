package sleppynavigators.studyupbackend.domain.event;

public sealed interface SystemEvent permits
        UserJoinEvent,
        UserLeaveEvent,
        ChallengeCreateEvent,
        ChallengeCompleteEvent {

    SystemEventType getType();
}

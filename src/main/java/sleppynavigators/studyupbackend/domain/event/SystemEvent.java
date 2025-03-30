package sleppynavigators.studyupbackend.domain.event;

import sleppynavigators.studyupbackend.domain.chat.SystemMessageTemplate;

public sealed interface SystemEvent permits
        UserJoinEvent,
        UserLeaveEvent,
        ChallengeCreateEvent,
        ChallengeCompleteEvent {

    SystemEventType getType();
    Long getGroupId();
    String generateMessage(SystemMessageTemplate template);
}

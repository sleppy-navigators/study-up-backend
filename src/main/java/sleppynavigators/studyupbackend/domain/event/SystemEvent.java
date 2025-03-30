package sleppynavigators.studyupbackend.domain.event;

import sleppynavigators.studyupbackend.domain.chat.SystemMessageTemplate;

public interface SystemEvent {

    SystemEventType getType();
    Long getGroupId();
    String generateMessage(SystemMessageTemplate template);
}

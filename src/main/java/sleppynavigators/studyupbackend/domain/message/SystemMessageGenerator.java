package sleppynavigators.studyupbackend.domain.message;

import sleppynavigators.studyupbackend.domain.event.SystemEvent;

public interface SystemMessageGenerator<T extends SystemEvent> {
    String generate(T event);
    boolean supports(T event);
}

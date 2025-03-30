package sleppynavigators.studyupbackend.application.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.application.chat.ChatMessageService;
import sleppynavigators.studyupbackend.domain.event.SystemEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class SystemEventListener {
    private final ChatMessageService chatMessageService;

    @EventListener
    public void handleSystemEvent(SystemEvent event) {
        try {
            chatMessageService.sendSystemMessage(event);
        } catch (Exception e) {
            log.error("시스템 메시지 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}

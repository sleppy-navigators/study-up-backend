package sleppynavigators.studyupbackend.application.event;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import sleppynavigators.studyupbackend.application.chat.ChatMessageService;
import sleppynavigators.studyupbackend.domain.event.SystemEvent;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SystemEventListener {
    private final ChatMessageService chatMessageService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSystemEvent(SystemEvent event) {
        try {
            chatMessageService.sendSystemMessage(event);
        } catch (Exception e) {
            log.error("시스템 메시지 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}

package sleppynavigators.studyupbackend.application.event;

import java.net.URL;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import sleppynavigators.studyupbackend.application.medium.MediumService;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.event.challenge.TaskCertifyEvent;
import sleppynavigators.studyupbackend.exception.database.EntityNotFoundException;
import sleppynavigators.studyupbackend.infrastructure.challenge.TaskRepository;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskEventListener {

    private final TaskRepository taskRepository;
    private final MediumService mediumService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    // NOTE: Transaction in this method cannot be committed
    public void handleTaskCertifyEvent(TaskCertifyEvent event) {
        try {
            Task task = taskRepository.findById(event.taskId())
                    .orElseThrow(() -> new EntityNotFoundException("Task not found: " + event.taskId()));
            List<URL> mediaUrls = task.getCertification().getImageUrls();
            for (URL mediaUrl : mediaUrls) {
                mediumService.storeMedia(mediaUrl);
            }
        } catch (Exception e) {
            log.error("Error handling TaskCertifyEvent: {}", e.getMessage(), e);
        }
    }
}

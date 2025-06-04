package sleppynavigators.studyupbackend.infrastructure.challenge.scheduler;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.application.challenge.TaskCertificationStatus;
import sleppynavigators.studyupbackend.application.event.NotificationEventPublisher;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.event.TaskFailEvent;
import sleppynavigators.studyupbackend.infrastructure.challenge.TaskQueryOptions;
import sleppynavigators.studyupbackend.infrastructure.challenge.TaskRepository;

@Slf4j
@Component
@RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class TaskScheduler {

    private final NotificationEventPublisher notificationEventPublisher;
    private final TaskRepository taskRepository;

    @Value("${scheduler.challenge.check-expiration.interval-minutes}")
    private long challengeCheckIntervalMinutes;

    @Scheduled(cron = "${scheduler.challenge.check-expiration.cron}", zone = "Asia/Seoul")
    @Transactional
    public void checkFailedTasks() {
        log.info("TaskScheduler - checkFailedTasks() started");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime baseTime = now.minusMinutes(challengeCheckIntervalMinutes);
        BooleanExpression predicate = TaskQueryOptions.getCompletedBetweenPredicate(baseTime, now)
                .and(TaskQueryOptions.getStatusPredicate(TaskCertificationStatus.FAILED));
        List<Task> failedTasks = taskRepository.findAll(predicate);

        for (Task task : failedTasks) {
            log.info("TaskScheduler - Processing failed task: {}", task.getId());
            TaskFailEvent event = new TaskFailEvent(
                    task.getChallenge().getOwner().getUserProfile().getUsername(),
                    task.getChallenge().getDetail().getTitle(),
                    task.getChallenge().getGroup().getId(),
                    task.getChallenge().getId(),
                    task.getChallenge().getOwner().getId()
            );
            notificationEventPublisher.publish(event);
        }
        log.info("TaskScheduler - checkFailedTasks() completed");
    }
}

package sleppynavigators.studyupbackend.infrastructure.challenge.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sleppynavigators.studyupbackend.application.event.NotificationEventPublisher;
import sleppynavigators.studyupbackend.common.ApplicationBaseTest;
import sleppynavigators.studyupbackend.common.support.ChallengeSupport;
import sleppynavigators.studyupbackend.common.support.GroupSupport;
import sleppynavigators.studyupbackend.common.support.UserSupport;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.event.TaskFailEvent;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;

@DisplayName("TaskScheduler 테스트")
public class TaskSchedulerTest extends ApplicationBaseTest {

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private GroupSupport groupSupport;

    @Autowired
    private ChallengeSupport challengeSupport;

    @MockitoBean
    private NotificationEventPublisher notificationEventPublisher;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = userSupport.registerUserToDB();
    }

    @Test
    @DisplayName("테스크 실패 확인 스케줄러 테스트")
    void checkFailedTasks() {
        // given
        Group groupToBelong = groupSupport.callToMakeGroup(List.of(currentUser));

        Challenge withFailedTasks = challengeSupport.callToMakeChallengeWithFailedTasks(
                groupToBelong, 5, currentUser);
        Challenge withCertifiedTasks = challengeSupport.callToMakeCompletedChallengeWithTasks(
                groupToBelong, 10, currentUser);

        clearInvocations(notificationEventPublisher);

        // when
        taskScheduler.checkFailedTasks();

        // then
        verify(notificationEventPublisher, times(5))
                .publish(any(TaskFailEvent.class));
    }
}

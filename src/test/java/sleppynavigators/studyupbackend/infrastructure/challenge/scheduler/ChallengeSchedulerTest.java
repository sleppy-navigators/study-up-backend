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
import sleppynavigators.studyupbackend.application.event.ChallengeEventPublisher;
import sleppynavigators.studyupbackend.common.ApplicationBaseTest;
import sleppynavigators.studyupbackend.common.support.ChallengeSupport;
import sleppynavigators.studyupbackend.common.support.GroupSupport;
import sleppynavigators.studyupbackend.common.support.UserSupport;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.event.ChallengeCompleteEvent;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;

@DisplayName("ChallengeScheduler 테스트")
public class ChallengeSchedulerTest extends ApplicationBaseTest {

    @Autowired
    private ChallengeScheduler challengeScheduler;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private GroupSupport groupSupport;

    @Autowired
    private ChallengeSupport challengeSupport;

    @MockitoBean
    private ChallengeEventPublisher challengeEventPublisher;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = userSupport.registerUserToDB();
    }

    @Test
    @DisplayName("챌린지 만료 확인 스케줄러 테스트")
    void checkExpiredChallenges() {
        // given
        Group groupToBelong = groupSupport.callToMakeGroup(List.of(currentUser));

        Challenge completed1 = challengeSupport.callToMakeCompletedChallengeWithTasks(
                groupToBelong, 10, currentUser);
        Challenge completed2 = challengeSupport.callToMakeCompletedChallengeWithTasks(
                groupToBelong, 10, currentUser);
        Challenge notCompleted = challengeSupport.callToMakeChallengesWithTasks(
                groupToBelong, 10, 0, currentUser);

        clearInvocations(challengeEventPublisher);

        // when
        challengeScheduler.checkExpiredChallenges();

        // then
        verify(challengeEventPublisher, times(2))
                .publishChallengeCompleteEvent(any(ChallengeCompleteEvent.class));
    }
}

package sleppynavigators.studyupbackend.infrastructure.challenge.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sleppynavigators.studyupbackend.application.event.SystemEventPublisher;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private SystemEventPublisher systemEventPublisher;

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

        Challenge completed1 = challengeSupport.callToMakeChallengesWithTasks(
                groupToBelong, 10, 5, currentUser);
        Challenge completed2 = challengeSupport.callToMakeChallengesWithTasks(
                groupToBelong, 10, 10, currentUser);
        Challenge notCompleted = challengeSupport.callToMakeChallengesWithTasks(
                groupToBelong, 10, 0, currentUser);

        jdbcTemplate.update("UPDATE challenges SET deadline = ? WHERE id = ?",
                LocalDateTime.now().minusHours(24), completed1.getId());
        jdbcTemplate.update("UPDATE challenges SET deadline = ? WHERE id = ?",
                LocalDateTime.now().minusHours(24), completed2.getId());
        jdbcTemplate.update("UPDATE challenges SET deadline = ? WHERE id = ?",
                LocalDateTime.now().plusHours(24), notCompleted.getId());

        clearInvocations(systemEventPublisher);

        // when
        challengeScheduler.checkExpiredChallenges();

        // then
        verify(systemEventPublisher, times(2))
                .publish(any(ChallengeCompleteEvent.class));
    }
}

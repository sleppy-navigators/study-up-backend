package sleppynavigators.studyupbackend.application.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import sleppynavigators.studyupbackend.application.event.SystemMessageEventListener;
import sleppynavigators.studyupbackend.common.ApplicationBaseTest;
import sleppynavigators.studyupbackend.common.support.BotSupport;
import sleppynavigators.studyupbackend.common.support.ChallengeSupport;
import sleppynavigators.studyupbackend.common.support.GroupSupport;
import sleppynavigators.studyupbackend.common.support.UserSupport;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.chat.Bot;
import sleppynavigators.studyupbackend.domain.event.ChallengeCancelEvent;
import sleppynavigators.studyupbackend.domain.event.ChallengeCreateEvent;
import sleppynavigators.studyupbackend.domain.event.TaskCertifyEvent;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.ChallengeCreationRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.ChallengeCreationRequest.TaskRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskCertificationRequest;

@DisplayName("ChallengeService 테스트")
class ChallengeServiceTest extends ApplicationBaseTest {

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private GroupSupport groupSupport;

    @Autowired
    private ChallengeSupport challengeSupport;

    @Autowired
    private BotSupport botSupport;

    @MockitoSpyBean
    private SystemMessageEventListener systemEventListener;

    private User testUser;

    private Group testGroup;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        testUser = userSupport.registerUserToDB();
        testGroup = groupSupport.registerGroupToDB(List.of(testUser));
        Bot testBot = botSupport.registerBotToDB(testGroup);
    }

    @Test
    @DisplayName("챌린지 생성 시 ChallengeCreateEvent가 발행된다")
    void createChallenge_PublishesChallengeCreateEvent() {
        // given
        ZonedDateTime deadline = ZonedDateTime.now().plusDays(7);
        TaskRequest taskRequest = new ChallengeCreationRequest.TaskRequest("testTask", deadline);
        ChallengeCreationRequest request = new ChallengeCreationRequest(
                "testChallenge", "description", List.of(taskRequest), 10L
        );

        clearInvocations(systemEventListener);

        // when
        challengeService.createChallenge(testUser.getId(), testGroup.getId(), request);

        // then
        verify(systemEventListener).handleSystemEvent(
                new ChallengeCreateEvent(testUser.getUserProfile().getUsername(),
                        "testChallenge",
                        testGroup.getId())
        );
    }

    @Test
    @DisplayName("챌린지 취소 시 ChallengeCancelEvent가 발행된다")
    void cancelChallenge_PublishesChallengeCancelEvent() {
        // given
        Challenge challenge = challengeSupport
                .callToMakeChallengesWithTasks(testGroup, 3, 2, testUser);

        clearInvocations(systemEventListener);

        // when
        challengeService.cancelChallenge(testUser.getId(), challenge.getId());

        // then
        verify(systemEventListener).handleSystemEvent(
                new ChallengeCancelEvent(
                        testUser.getUserProfile().getUsername(),
                        challenge.getDetail().getTitle(),
                        testGroup.getId())
        );
    }

    @Test
    @DisplayName("테스크 인증 자료 제출 시 TaskCertifiedEvent가 발행된다")
    void certifyTask_PublishesTaskCertifiedEvent() throws MalformedURLException {
        // given
        Challenge challenge = challengeSupport
                .callToMakeChallengesWithTasks(testGroup, 3, 0, testUser);
        TaskCertificationRequest taskCertificationRequest =
                new TaskCertificationRequest(List.of(new URL("https://blog.com/article")), List.of()
                );

        clearInvocations(systemEventListener);

        // when
        challengeService.completeTask(testUser.getId(), challenge.getId(), 1L, taskCertificationRequest);

        // then
        verify(systemEventListener).handleSystemEvent(
                new TaskCertifyEvent(
                        testUser.getUserProfile().getUsername(),
                        challenge.getTasks().get(0).getDetail().getTitle(),
                        challenge.getDetail().getTitle(),
                        testGroup.getId())
        );
    }

    @Test
    @DisplayName("챌린지 완료 시 남은 보증금에 따라 챌린저에게 포인트를 지급한다")
    void completeChallenge_Success() {
        // given
        Challenge challenge = challengeSupport
                .callToMakeCompletedChallengeWithTasks(testGroup, 3, testUser);
        User challenger = challenge.getOwner();
        Long initialChallengerEquity = challenger.getPoint().getAmount();
        Long remainingDeposit = challenge.getDeposit().getAmount();

        // when
        challengeService.settlementReward(challenger.getId(), challenge.getId());

        // then
        assertThat(userRepository.findById(challenger.getId()).orElseThrow().getPoint().getAmount())
                .isEqualTo(Math.round(initialChallengerEquity + remainingDeposit * 1.1));
    }
}

package sleppynavigators.studyupbackend.application.challenge;

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
import sleppynavigators.studyupbackend.application.event.SystemEventListener;
import sleppynavigators.studyupbackend.common.ApplicationBaseTest;
import sleppynavigators.studyupbackend.common.support.BotSupport;
import sleppynavigators.studyupbackend.common.support.ChallengeSupport;
import sleppynavigators.studyupbackend.common.support.GroupSupport;
import sleppynavigators.studyupbackend.common.support.UserSupport;
import sleppynavigators.studyupbackend.domain.bot.Bot;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.event.ChallengeCancelEvent;
import sleppynavigators.studyupbackend.domain.event.ChallengeCompleteEvent;
import sleppynavigators.studyupbackend.domain.event.ChallengeCreateEvent;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
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
    private SystemEventListener systemEventListener;

    private User testUser;

    private Group testGroup;

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
                "testChallenge", deadline, "description", List.of(taskRequest)
        );

        // when
        challengeService.createChallenge(testUser.getId(), testGroup.getId(), request);

        // then
        verify(systemEventListener).handleSystemEvent(
                new ChallengeCreateEvent(testUser.getUserProfile().username(), "testChallenge", testGroup.getId())
        );
    }

    @Test
    @DisplayName("모든 태스크 완료 시 ChallengeCompleteEvent가 발행된다")
    void completeAllTasks_PublishesChallengeCompleteEvent() throws MalformedURLException {
        // given
        Challenge challenge = challengeSupport
                .callToMakeChallengesWithTasks(testGroup, 3, 2, testUser);
        Task taskToCertify = challenge.getTasks().get(2);

        TaskCertificationRequest request = new TaskCertificationRequest(
                List.of(new URL("http://example.com")),
                List.of(new URL("http://example.com/image"))
        );

        // when
        challengeService.completeTask(testUser.getId(), challenge.getId(), taskToCertify.getId(), request);

        // then
        verify(systemEventListener).handleSystemEvent(new ChallengeCompleteEvent(
                testUser.getUserProfile().username(), challenge.getDetail().title(), testGroup.getId()));
    }

    @Test
    @DisplayName("챌린지 취소 시 ChallengeCancelEvent가 발행된다")
    void cancelChallenge_PublishesChallengeCancelEvent() {
        // given
        Challenge challenge = challengeSupport
                .callToMakeChallengesWithTasks(testGroup, 3, 2, testUser);

        // when
        challengeService.cancelChallenge(testUser.getId(), challenge.getId());

        // then
        verify(systemEventListener).handleSystemEvent(
                new ChallengeCreateEvent(
                        testUser.getUserProfile().username(),
                        challenge.getDetail().title(),
                        testGroup.getId())
        );
        verify(systemEventListener).handleSystemEvent(
                new ChallengeCancelEvent(
                        testUser.getUserProfile().username(),
                        challenge.getDetail().title(),
                        testGroup.getId())
        );
    }
}

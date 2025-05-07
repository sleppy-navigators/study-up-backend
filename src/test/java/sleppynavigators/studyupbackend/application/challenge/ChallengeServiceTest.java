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
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.chat.Bot;
import sleppynavigators.studyupbackend.domain.event.ChallengeCancelEvent;
import sleppynavigators.studyupbackend.domain.event.ChallengeCreateEvent;
import sleppynavigators.studyupbackend.domain.event.TaskCertifiedEvent;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.ChallengeCreationRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.ChallengeCreationRequest.TaskRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskCertificationRequest;

@DisplayName("[애플리케이션] ChallengeService 테스트")
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
    @DisplayName("챌린지 생성 이벤트 발행 - 성공")
    void createChallenge_PublishesChallengeCreateEvent() {
        // given
        ZonedDateTime deadline = ZonedDateTime.now().plusDays(7);
        TaskRequest taskRequest = new ChallengeCreationRequest.TaskRequest("testTask", deadline);
        ChallengeCreationRequest request = new ChallengeCreationRequest(
                "testChallenge", "description", List.of(taskRequest));

        // when
        challengeService.createChallenge(testUser.getId(), testGroup.getId(), request);

        // then
        verify(systemEventListener).handleSystemEvent(
                new ChallengeCreateEvent(testUser.getUserProfile().getUsername(),
                        "testChallenge",
                        testGroup.getId()));
    }

    @Test
    @DisplayName("챌린지 취소 이벤트 발행 - 성공")
    void cancelChallenge_PublishesChallengeCancelEvent() {
        // given
        Challenge challenge = challengeSupport
                .callToMakeChallengesWithTasks(testGroup, 3, 2, testUser);

        // when
        challengeService.cancelChallenge(testUser.getId(), challenge.getId());

        // then
        verify(systemEventListener).handleSystemEvent(
                new ChallengeCreateEvent(
                        testUser.getUserProfile().getUsername(),
                        challenge.getDetail().getTitle(),
                        testGroup.getId()));
        verify(systemEventListener).handleSystemEvent(
                new ChallengeCancelEvent(
                        testUser.getUserProfile().getUsername(),
                        challenge.getDetail().getTitle(),
                        testGroup.getId()));
    }

    @Test
    @DisplayName("테스크 인증 이벤트 발행 - 성공")
    void certifyTask_PublishesTaskCertifiedEvent() throws MalformedURLException {
        // given
        Challenge challenge = challengeSupport
                .callToMakeChallengesWithTasks(testGroup, 3, 0, testUser);
        TaskCertificationRequest taskCertificationRequest = new TaskCertificationRequest(
                List.of(new URL("https://blog.com/article")), List.of());

        // when
        challengeService.completeTask(testUser.getId(), challenge.getId(), 1L, taskCertificationRequest);

        // then
        verify(systemEventListener).handleSystemEvent(
                new ChallengeCreateEvent(
                        testUser.getUserProfile().getUsername(),
                        challenge.getDetail().getTitle(),
                        testGroup.getId()));
        verify(systemEventListener).handleSystemEvent(
                new TaskCertifiedEvent(
                        testUser.getUserProfile().getUsername(),
                        challenge.getTasks().get(0).getDetail().getTitle(),
                        challenge.getDetail().getTitle(),
                        testGroup.getId()));
    }
}

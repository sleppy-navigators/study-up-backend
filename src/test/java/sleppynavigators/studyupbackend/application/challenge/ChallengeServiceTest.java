package sleppynavigators.studyupbackend.application.challenge;

import static org.mockito.Mockito.verify;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import sleppynavigators.studyupbackend.application.event.SystemEventListener;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.event.ChallengeCompleteEvent;
import sleppynavigators.studyupbackend.domain.event.ChallengeCreateEvent;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.infrastructure.challenge.ChallengeRepository;
import sleppynavigators.studyupbackend.infrastructure.challenge.TaskRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.ChallengeCreationRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskCertificationRequest;
import sleppynavigators.studyupbackend.presentation.common.DatabaseCleaner;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("ChallengeService 테스트")
class ChallengeServiceTest {

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoSpyBean
    private SystemEventListener systemEventListener;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private User testUser;
    private Group testGroup;

    @BeforeEach
    void setUp() {
        databaseCleaner.execute();
        testUser = userRepository.save(new User("testUser", "test@test.com"));
        testGroup = Group.builder()
            .name("testGroup")
            .description("description")
            .creator(testUser)
            .build();
        testGroup = groupRepository.save(testGroup);
    }

    @Test
    @DisplayName("챌린지 생성 시 ChallengeCreateEvent가 발행된다")
    void createChallenge_PublishesChallengeCreateEvent() {
        // given
        LocalDateTime deadline = LocalDateTime.now().plusDays(7);
        ChallengeCreationRequest.TaskRequest taskRequest = 
            new ChallengeCreationRequest.TaskRequest("testTask", deadline);
        ChallengeCreationRequest request = new ChallengeCreationRequest(
            "testChallenge", deadline, "description", List.of(taskRequest)
        );

        // when
        challengeService.createChallenge(testUser.getId(), testGroup.getId(), request);

        // then
        verify(systemEventListener).handleSystemEvent(
            new ChallengeCreateEvent("testUser", "testChallenge", testGroup.getId())
        );
    }

    @Test
    @DisplayName("모든 태스크 완료 시 ChallengeCompleteEvent가 발행된다")
    void completeAllTasks_PublishesChallengeCompleteEvent() throws MalformedURLException {
        // given
        Challenge challenge = Challenge.builder()
            .owner(testUser)
            .group(testGroup)
            .title("testChallenge")
            .deadline(LocalDateTime.now().plusDays(7))
            .description("description")
            .build();
        challenge.addTask("testTask", LocalDateTime.now().plusDays(7));
        Challenge savedChallenge = challengeRepository.save(challenge);
        Task task = savedChallenge.getTasksForUser(testUser).get(0);

        TaskCertificationRequest request = new TaskCertificationRequest(
            List.of(new URL("http://example.com")),
            List.of(new URL("http://example.com/image"))
        );

        // when
        challengeService.completeTask(testUser.getId(), savedChallenge.getId(), task.getId(), request);

        // then
        verify(systemEventListener).handleSystemEvent(
            new ChallengeCompleteEvent("testUser", "testChallenge", testGroup.getId())
        );
    }
}

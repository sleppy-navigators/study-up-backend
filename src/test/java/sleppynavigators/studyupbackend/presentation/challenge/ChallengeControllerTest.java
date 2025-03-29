package sleppynavigators.studyupbackend.presentation.challenge;

import static io.restassured.RestAssured.with;
import static io.restassured.http.Method.GET;
import static io.restassured.http.Method.POST;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.ExtractableResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.common.RestAssuredBaseTest;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessToken;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessTokenProperties;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;
import sleppynavigators.studyupbackend.infrastructure.challenge.ChallengeRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskCertificationRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskListResponse;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskListResponse.TaskListItem;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskResponse;

@DisplayName("ChallengeController API 테스트")
public class ChallengeControllerTest extends RestAssuredBaseTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private AccessTokenProperties accessTokenProperties;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = TestFixtureMother.registerUser(userRepository);
        String bearerToken = TestFixtureMother.createBearerToken(currentUser, accessTokenProperties);
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", bearerToken)
                .build();
    }

    @Test
    @DisplayName("챌린지 테스크 목록 조회")
    void getTasks_Success() {
        // given
        Challenge challengeToQuery = TestFixtureMother.registerChallengeWithTasks(
                new int[]{3, 1}, currentUser, groupRepository, challengeRepository);

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/challenges/{challengeId}/tasks", challengeToQuery.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", TaskListResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.tasks()).hasSize(3);
                    assertThat(data.tasks()).map(TaskListItem::certification).anyMatch(Objects::nonNull);
                    assertThat(data.tasks()).map(TaskListItem::certification).anyMatch(Objects::isNull);
                });
    }

    @Test
    @DisplayName("챌린지 테스크 완료")
    void completeTask_Success() throws MalformedURLException {
        // given
        Challenge challengeToQuery = TestFixtureMother.registerChallengeWithTasks(
                new int[]{3, 1}, currentUser, groupRepository, challengeRepository);
        Task taskToCertify = challengeToQuery.getTasks().get(1);

        TaskCertificationRequest request = new TaskCertificationRequest(
                List.of(new URL("https://test.com")), List.of());

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/challenges/{challengeId}/tasks/{taskId}/certify",
                        challengeToQuery.getId(), taskToCertify.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", TaskResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.certification()).isNotNull();
                });
    }

    @Transactional
    private static class TestFixtureMother {

        /**
         * Generate a test user and save it to the database. `username` and `email` are set to "test-user" and
         * "test-email" respectively.
         *
         * @param userRepository UserRepository to save the user
         * @return The saved user
         */
        static User registerUser(UserRepository userRepository) {
            User user = new User("test-user", "test-email");
            return userRepository.save(user);
        }

        /**
         * Generate access token for the given user. The token can be used to authenticate the user for testing.
         *
         * @param user                  The user for testing
         * @param accessTokenProperties The properties for the access token
         * @return The generated access token as a string
         */
        static String createBearerToken(User user, AccessTokenProperties accessTokenProperties) {
            Long userId = user.getId();
            UserProfile userProfile = user.getUserProfile();
            List<String> authorities = List.of("profile");

            String accessToken = new AccessToken(userId, userProfile, authorities, accessTokenProperties)
                    .serialize(accessTokenProperties);
            return "Bearer " + accessToken;
        }

        /**
         * Generate a test challenge with tasks and save it to the database. The challenge is created by the given user
         * and belongs to a new group. The tasks are created with the given progress, where the first element is the
         * number of tasks and the second element is the number of certified tasks.
         *
         * @param taskProgress        The progress of the tasks, where the first element is the number of tasks and the
         *                            second element is the number of certified tasks
         * @param creator             The user who created the groups
         * @param groupRepository     GroupRepository to save the groups
         * @param challengeRepository ChallengeRepository to save the challenges
         * @return The generated challenge with tasks
         */
        static Challenge registerChallengeWithTasks(
                int[] taskProgress, User creator,
                GroupRepository groupRepository, ChallengeRepository challengeRepository) {

            Group group = Group.builder()
                    .name("test-group")
                    .description("test-group-description")
                    .creator(creator)
                    .build();
            groupRepository.save(group);

            Challenge challenge = Challenge.builder()
                    .title("test-challenge")
                    .deadline(LocalDateTime.now().plusDays(3))
                    .group(group)
                    .owner(creator)
                    .build();

            int numOfTasks = taskProgress[0];
            int numOfCertified = taskProgress[1];
            for (int ti = 0; ti < numOfTasks; ti++) {
                challenge.addTask("test-task-" + ti, LocalDateTime.now().plusHours(3));

                if (ti < numOfCertified) {
                    try {
                        challenge.getTasks().get(ti)
                                .certify(List.of(), List.of(new URL("https://test.com")), creator);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            challengeRepository.save(challenge);
            return challenge;
        }
    }
}

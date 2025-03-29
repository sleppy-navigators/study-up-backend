package sleppynavigators.studyupbackend.presentation.user;

import static io.restassured.RestAssured.with;
import static io.restassured.http.Method.GET;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.ExtractableResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;
import sleppynavigators.studyupbackend.infrastructure.challenge.ChallengeRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupListResponse;
import sleppynavigators.studyupbackend.presentation.user.dto.response.UserTaskListResponse;
import sleppynavigators.studyupbackend.presentation.user.dto.response.UserTaskListResponse.UserTaskListItem;

@DisplayName("UserController API 테스트")
public class UserControllerTest extends RestAssuredBaseTest {

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
    @DisplayName("사용자가 그룹 목록 조회에 성공한다")
    void getGroups_Success() {
        // given
        List<Group> groups = TestFixtureMother.registerGroupsWithChallengesAndTasks(List.of(
                        List.of(new int[]{3, 1}, new int[]{2, 0}, new int[]{1, 0}),
                        List.of(new int[]{2, 2}),
                        List.of(new int[]{0, 0}, new int[]{4, 3}, new int[]{5, 4}, new int[]{6, 5})),
                currentUser, groupRepository, challengeRepository);

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/users/me/groups")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", GroupListResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.groups()).hasSize(3);
                });
    }

    @Test
    @DisplayName("사용자가 테스크 목록 조회에 성공한다")
    void getTasks_Success() {
        // given
        List<Group> groups = TestFixtureMother.registerGroupsWithChallengesAndTasks(List.of(
                        List.of(new int[]{3, 1}, new int[]{2, 0}, new int[]{1, 0}),
                        List.of(new int[]{2, 2}),
                        List.of(new int[]{0, 0}, new int[]{4, 3}, new int[]{5, 4}, new int[]{6, 5})),
                currentUser, groupRepository, challengeRepository);

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/users/me/tasks")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", UserTaskListResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.tasks()).hasSize(23);
                    assertThat(data.tasks()).map(UserTaskListItem::certification).anyMatch(Objects::nonNull);
                    assertThat(data.tasks()).map(UserTaskListItem::certification).anyMatch(Objects::isNull);
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
         * Generate a list of groups with challenges and tasks. Each group contains a list of challenges, and each
         * challenge contains a list of tasks. The tasks are organized by their progress, where the first element of the
         * array is the number of tasks and the second element is the number of certified tasks.
         *
         * @param groupChallengeListOrganizedByTaskProgress A list of challenges organized by task progress
         * @param creator                                   The user who created the groups
         * @param groupRepository                           GroupRepository to save the groups
         * @param challengeRepository                       ChallengeRepository to save the challenges
         * @return A list of groups with challenges and tasks
         */
        static List<Group> registerGroupsWithChallengesAndTasks(
                List<List<int[]>> groupChallengeListOrganizedByTaskProgress, User creator,
                GroupRepository groupRepository, ChallengeRepository challengeRepository) {

            List<Group> groups = new ArrayList<>();
            int numOfGroups = groupChallengeListOrganizedByTaskProgress.size();
            for (int gi = 0; gi < numOfGroups; gi++) {
                Group group = Group.builder()
                        .name("test-group-" + gi)
                        .description("test-group-description-" + gi)
                        .creator(creator)
                        .build();
                groups.add(group);
                groupRepository.save(group);

                for (int ci = 0; ci < groupChallengeListOrganizedByTaskProgress.get(gi).size(); ci++) {
                    int[] taskProgress = groupChallengeListOrganizedByTaskProgress.get(gi).get(ci);
                    Challenge challenge = Challenge.builder()
                            .title("test-challenge-" + gi + "-" + ci)
                            .deadline(LocalDateTime.now().plusDays(3))
                            .group(group)
                            .owner(creator)
                            .build();

                    int numOfTasks = taskProgress[0];
                    int numOfCertified = taskProgress[1];
                    for (int ti = 0; ti < numOfTasks; ti++) {
                        challenge.addTask("test-task-" + gi + "-" + ci + "-" + ti, LocalDateTime.now().plusHours(3));

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
                }
            }
            return groups;
        }
    }
}

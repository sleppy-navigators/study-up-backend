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
    void getGroups_Success() throws MalformedURLException {
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
    void getTasks_Success() throws MalformedURLException {
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
    static class TestFixtureMother {

        static User registerUser(UserRepository userRepository) {
            User user = new User("test-user", "test-email");
            return userRepository.save(user);
        }

        static String createBearerToken(User user, AccessTokenProperties accessTokenProperties) {
            Long userId = user.getId();
            UserProfile userProfile = user.getUserProfile();
            List<String> authorities = List.of("profile");

            String accessToken = new AccessToken(userId, userProfile, authorities, accessTokenProperties)
                    .serialize(accessTokenProperties);
            return "Bearer " + accessToken;
        }

        static List<Group> registerGroupsWithChallengesAndTasks(
                List<List<int[]>> groupChallengeListOrganizedByTaskProgress, User creator,
                GroupRepository groupRepository, ChallengeRepository challengeRepository)
                throws MalformedURLException {

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

                for (int[] taskProgress : groupChallengeListOrganizedByTaskProgress.get(gi)) {
                    Challenge challenge = Challenge.builder()
                            .title("test-challenge-" + gi)
                            .deadline(LocalDateTime.now().plusDays(3))
                            .group(group)
                            .owner(creator)
                            .build();

                    int numOfTasks = taskProgress[0];
                    int numOfCertified = taskProgress[1];
                    for (int ti = 0; ti < numOfTasks; ti++) {
                        challenge.addTask("test-task-" + gi + "-" + ti, LocalDateTime.now().plusHours(3));

                        if (ti < numOfCertified) {
                            challenge.getTasks().get(ti)
                                    .certify(List.of(), List.of(new URL("https://test.com")), creator);
                        }
                    }
                    challengeRepository.save(challenge);
                }
            }
            return groups;
        }
    }
}

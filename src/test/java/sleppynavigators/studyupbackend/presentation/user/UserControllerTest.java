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
        List<Group> groups = TestFixtureMother
                .registerGroupsWithChallengesAndTasks(
                        List.of(3, 2, 1, 2, 0, 4, 5, 6),
                        List.of(1, 0, 0, 2, 0, 3, 4, 5),
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
                    assertThat(data.groups()).hasSize(8);
                });
    }

    @Test
    @DisplayName("사용자가 테스크 목록 조회에 성공한다")
    void getTasks_Success() throws MalformedURLException {
        // given
        List<Group> groups = TestFixtureMother
                .registerGroupsWithChallengesAndTasks(
                        List.of(3, 2, 1, 2, 0, 4, 5, 6),
                        List.of(1, 0, 0, 2, 0, 3, 4, 5),
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
                List<Integer> numOfTasks, List<Integer> numOfCertified, User creator,
                GroupRepository groupRepository, ChallengeRepository challengeRepository)
                throws MalformedURLException {

            List<Group> groups = new ArrayList<>();

            for (int i = 0; i < numOfTasks.size(); i++) {
                Group group = Group.builder()
                        .name("test-group-" + i)
                        .description("test-group-description-" + i)
                        .creator(creator)
                        .build();
                groups.add(group);
                groupRepository.save(group);

                Challenge challenge = Challenge.builder()
                        .title("test-challenge-" + i)
                        .deadline(LocalDateTime.now().plusDays(3))
                        .group(group)
                        .owner(creator)
                        .build();

                for (int j = 0; j < numOfTasks.get(i); j++) {
                    challenge.addTask("test-task-" + i + "-" + j, LocalDateTime.now().plusHours(3));
                    if (j < numOfCertified.get(i)) {
                        challenge.getTasks().get(j).certify(List.of(), List.of(new URL("https://test.com")), creator);
                    }
                }
                challengeRepository.save(challenge);
            }
            return groups;
        }
    }
}

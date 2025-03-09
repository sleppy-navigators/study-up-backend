package sleppynavigators.studyupbackend.presentation.user;

import static io.restassured.RestAssured.with;
import static io.restassured.http.Method.GET;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessToken;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessTokenProperties;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;
import sleppynavigators.studyupbackend.infrastructure.challenge.ChallengeRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.ChallengeCreationRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.ChallengeCreationRequest.TaskRequest;
import sleppynavigators.studyupbackend.presentation.common.DatabaseCleaner;
import sleppynavigators.studyupbackend.presentation.user.dto.response.UserTaskListResponse.UserTaskListItem;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("UserController API 테스트")
public class UserControllerTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private AccessTokenProperties accessTokenProperties;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @LocalServerPort
    private int port;

    private User currentUser;

    @BeforeEach
    void setUp() {
        UserProfile userProfile = new UserProfile("guest", "example@guest.com");
        currentUser = userRepository.save(new User("guest", "example@guest.com"));

        AccessToken accessToken =
                new AccessToken(currentUser.getId(), userProfile, List.of("profile"), accessTokenProperties);
        String bearerToken = "Bearer " + accessToken.serialize(accessTokenProperties);

        RestAssured.port = port;
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", bearerToken)
                .build();
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.execute();
        RestAssured.reset();
    }

    @Test
    @DisplayName("사용자가 그룹 목록 조회에 성공한다")
    void getGroups_Success() {
        // given
        groupRepository.saveAll(
                List.of(new Group("test group1", "test description", "https://test.com", currentUser),
                        new Group("test group2", "test description", "https://test.com", currentUser),
                        new Group("test group3", "test description", "https://test.com", currentUser),
                        new Group("test group4", "test description", "https://test.com", currentUser)));

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/users/me/groups")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getList("data.groups")).hasSize(4);
        assertThat(response.jsonPath().getString("data.groups[].id")).isNotBlank();
        assertThat(response.jsonPath().getString("data.groups[].name")).isNotBlank();
        assertThat(response.jsonPath().getString("data.groups[].description")).isNotBlank();
        assertThat(response.jsonPath().getString("data.groups[].thumbnailUrl")).isNotBlank();
    }

    @Test
    @DisplayName("사용자가 테스크 목록 조회에 성공한다")
    void getTasks_Success() throws MalformedURLException {
        // given
        Group group1 = groupRepository.save(
                new Group("test group1", "test description", "https://test.com", currentUser));
        Group group2 = groupRepository.save(
                new Group("test group2", "test description", "https://test.com", currentUser));
        Group group3 = groupRepository.save(
                new Group("test group3", "test description", "https://test.com", currentUser));

        Challenge challenge1 = challengeRepository.save(new ChallengeCreationRequest("test challenge 1",
                LocalDateTime.now().plusDays(3), null, List.of(
                new TaskRequest("test task 1-1-1", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 1-1-2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 1-1-3", LocalDateTime.now().plusHours(9))
        )).toEntity(currentUser, group1));
        Challenge challenge2 = challengeRepository.save(new ChallengeCreationRequest("test challenge 2",
                LocalDateTime.now().plusDays(3), null, List.of(
                new TaskRequest("test task 1-2-1", LocalDateTime.now().plusHours(3))
        )).toEntity(currentUser, group1));
        Challenge challenge3 = challengeRepository.save(new ChallengeCreationRequest("test challenge 3",
                LocalDateTime.now().plusDays(3), null, List.of(
                new TaskRequest("test task 3-3-1", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 3-3-2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 3-3-3", LocalDateTime.now().plusHours(9)),
                new TaskRequest("test task 3-3-4", LocalDateTime.now().plusHours(12))
        )).toEntity(currentUser, group3));

        challenge1.getTasks().get(0).certify(List.of(), List.of(new URL("https://test.com")));
        challenge2.getTasks().get(0)
                .certify(List.of(new URL("https://test.com"), new URL("https://test2.com")),
                        List.of(new URL("https://test.com")));
        challengeRepository.saveAll(List.of(challenge1, challenge2, challenge3));

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/users/me/tasks")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getList("data.tasks")).hasSize(8);
        assertThat(response.jsonPath().getList("data.tasks", UserTaskListItem.class))
                .anySatisfy(task -> assertThat(task.certification()).isNotNull());
        assertThat(response.jsonPath().getList("data.tasks", UserTaskListItem.class))
                .anySatisfy(task -> assertThat(task.certification()).isNull());
    }
}

package sleppynavigators.studyupbackend.presentation.challenge;

import static io.restassured.RestAssured.with;
import static io.restassured.http.Method.GET;
import static io.restassured.http.Method.POST;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskCertificationRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskCertificationDTO;
import sleppynavigators.studyupbackend.presentation.common.DatabaseCleaner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("ChallengeController API 테스트")
public class ChallengeControllerTest {

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
    @DisplayName("챌린지 테스크 목록 조회")
    void getTasks_Success() throws MalformedURLException {
        // given
        Group group = groupRepository.save(Group.builder()
                .name("test group")
                .description("test description")
                .thumbnailUrl("https://test.com")
                .creator(currentUser)
                .build());

        Challenge challenge = challengeRepository.save(new ChallengeCreationRequest("test challenge 1",
                LocalDateTime.now().plusDays(3), null, List.of(
                new TaskRequest("test task 1", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 3", LocalDateTime.now().plusHours(9))
        )).toEntity(currentUser, group));

        challenge.getTasks().get(0).certify(List.of(), List.of(new URL("https://test.com")), currentUser);
        challenge.getTasks().get(2).certify(List.of(new URL("https://test.com")),
                List.of(new URL("https://test.com"),
                        new URL("https://test2.com"),
                        new URL("https://test3.com")), currentUser);
        challengeRepository.save(challenge);

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/challenges/{challengeId}/tasks", challenge.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getList("data.tasks")).hasSize(3);
        assertThat(response.jsonPath().getList("data.tasks.id", Long.class)).noneMatch(Objects::isNull);
        assertThat(response.jsonPath().getList("data.tasks.title", String.class)).noneMatch(String::isBlank);
        assertThat(response.jsonPath().getList("data.tasks.deadline", String.class)).noneMatch(String::isBlank);
        assertThat(response.jsonPath().getList("data.tasks.certification", TaskCertificationDTO.class))
                .anyMatch(Objects::nonNull);
        assertThat(response.jsonPath().getList("data.tasks.certification", TaskCertificationDTO.class))
                .anyMatch(Objects::isNull);
    }

    @Test
    @DisplayName("챌린지 테스크 완료")
    void completeTask_Success() throws MalformedURLException {
        // given
        Group group = groupRepository.save(Group.builder()
                .name("test group")
                .description("test description")
                .thumbnailUrl("https://test.com")
                .creator(currentUser)
                .build());

        Challenge challenge = challengeRepository.save(new ChallengeCreationRequest("test challenge 1",
                LocalDateTime.now().plusDays(3), null, List.of(
                new TaskRequest("test task 1", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 3", LocalDateTime.now().plusHours(9))
        )).toEntity(currentUser, group));

        challenge.getTasks().get(0).certify(List.of(), List.of(new URL("https://test.com")), currentUser);
        challenge.getTasks().get(2).certify(List.of(new URL("https://test.com")),
                List.of(new URL("https://test.com"),
                        new URL("https://test2.com"),
                        new URL("https://test3.com")), currentUser);
        challengeRepository.save(challenge);

        TaskCertificationRequest request = new TaskCertificationRequest(
                List.of(new URL("https://test.com")), List.of());

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/challenges/{challengeId}/tasks/{taskId}/certify",
                        challenge.getId(), challenge.getTasks().get(1).getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getLong("data.id")).isNotNull();
        assertThat(response.jsonPath().getString("data.title")).isNotBlank();
        assertThat(response.jsonPath().getString("data.deadline")).isNotBlank();
        assertThat(response.jsonPath().getObject("data.certification", TaskCertificationDTO.class)).isNotNull();
        assertThat(response.jsonPath().getString("data.certification.certificatedAt")).isNotBlank();
    }
}

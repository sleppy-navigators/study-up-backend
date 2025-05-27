package sleppynavigators.studyupbackend.presentation.challenge;

import static io.restassured.RestAssured.with;
import static io.restassured.http.Method.DELETE;
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
import org.springframework.jdbc.core.JdbcTemplate;
import sleppynavigators.studyupbackend.application.challenge.TaskCertificationStatus;
import sleppynavigators.studyupbackend.common.RestAssuredBaseTest;
import sleppynavigators.studyupbackend.common.support.AuthSupport;
import sleppynavigators.studyupbackend.common.support.ChallengeSupport;
import sleppynavigators.studyupbackend.common.support.GroupSupport;
import sleppynavigators.studyupbackend.common.support.UserSupport;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.ErrorCode;
import sleppynavigators.studyupbackend.infrastructure.challenge.ChallengeRepository;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskCertificationRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.HuntingResponse;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskListResponse;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskListResponse.TaskListItem;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskResponse;

@DisplayName("ChallengeController API 테스트")
public class ChallengeControllerTest extends RestAssuredBaseTest {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private AuthSupport authSupport;

    @Autowired
    private GroupSupport groupSupport;

    @Autowired
    private ChallengeSupport challengeSupport;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = userSupport.registerUserToDB();
        String bearerToken = authSupport.createBearerToken(currentUser);
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", bearerToken)
                .build();
    }

    @Test
    @DisplayName("챌린지 생성 24시간 이내 삭제 성공")
    void cancelChallenge_Success() {
        // given
        Group groupToBelong = groupSupport.callToMakeGroup(List.of(currentUser));
        Challenge challengeToCancel = challengeSupport
                .callToMakeChallengesWithTasks(groupToBelong, 3, 1, currentUser);

        // when
        ExtractableResponse<?> response = with()
                .when().request(DELETE, "/challenges/{challengeId}", challengeToCancel.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(challengeRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("챌린지 생성 24시간이 초과하면 삭제 실패")
    void cancelChallenge_Fail() {
        // given
        Group groupToBelong = groupSupport.callToMakeGroup(List.of(currentUser));
        Challenge challengeToCancel = challengeSupport
                .callToMakeChallengesWithTasks(groupToBelong, 3, 1, currentUser);

        // when
        jdbcTemplate.update("UPDATE challenges SET created_at = ? WHERE id = ?",
                LocalDateTime.now().minusHours(24), challengeToCancel.getId());

        ExtractableResponse<?> response = with()
                .when().request(DELETE, "/challenges/{challengeId}", challengeToCancel.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.FORBIDDEN_CONTENT.getCode());
        assertThat(response.jsonPath().getString("message"))
                .contains("User cannot modify this challenge");
    }

    @Test
    @DisplayName("챌린지 테스크 목록 조회")
    void getTasks_Success() {
        // given
        Group groupToBelong = groupSupport.callToMakeGroup(List.of(currentUser));
        Challenge challengeToQuery = challengeSupport
                .callToMakeChallengesWithTasks(groupToBelong, 3, 1, currentUser);

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
    @DisplayName("챌린지 테스크 목록 조회 - 인증된 테스크만")
    void getTasks_Certified_Success() {
        // given
        Group groupToBelong = groupSupport.callToMakeGroup(List.of(currentUser));
        Challenge challengeToQuery = challengeSupport
                .callToMakeChallengesWithTasks(groupToBelong, 3, 1, currentUser);

        // when
        ExtractableResponse<?> response = with()
                .queryParam("status", TaskCertificationStatus.SUCCEED)
                .when().request(GET, "/challenges/{challengeId}/tasks", challengeToQuery.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", TaskListResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.tasks()).hasSize(1);
                    assertThat(data.tasks()).map(TaskListItem::certification).allMatch(Objects::nonNull);
                });
    }

    @Test
    @DisplayName("챌린지 테스크 완료")
    void completeTask_Success() throws MalformedURLException {
        // given
        Group groupToBelong = groupSupport.callToMakeGroup(List.of(currentUser));
        Challenge challengeToQuery = challengeSupport
                .callToMakeChallengesWithTasks(groupToBelong, 3, 1, currentUser);
        Task taskToCertify = challengeToQuery.getTasks().get(1);

        TaskCertificationRequest request =
                new TaskCertificationRequest(
                        List.of(new URL("https://blog.com/article1"),
                                new URL("https://blog.com/article2")),
                        List.of(new URL("https://sns.com/image1"),
                                new URL("https://sns.com/image2"),
                                new URL("https://sns.com/image3")));

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

    @Test
    @DisplayName("챌린지 테스크 헌팅 성공")
    void huntingTask_Success() {
        // given
        User challenger = userSupport.registerUserToDB();
        Group groupToBelong = groupSupport.callToMakeGroup(List.of(currentUser, challenger));
        Challenge challengeToQuery = challengeSupport
                .callToMakeChallengeWithFailedTasks(groupToBelong, 3, challenger);
        Task taskToHunt = challengeToQuery.getTasks().get(0);

        // when
        ExtractableResponse<?> response = with()
                .when().request(POST, "/challenges/{challengeId}/tasks/{taskId}/hunt",
                        challengeToQuery.getId(), taskToHunt.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", HuntingResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.point())
                            .isEqualTo(challengeToQuery.getDeposit().getInitialAmount() / 3 / 1);
                });
    }

    @Test
    @DisplayName("챌린지 테스크 헌팅 실패 - 실패하지 않은 테스크는 헌팅할 수 없음")
    void huntingTask_Fail() {
        // given
        User challenger = userSupport.registerUserToDB();
        Group groupToBelong = groupSupport.callToMakeGroup(List.of(currentUser, challenger));
        Challenge challengeToQuery = challengeSupport
                .callToMakeChallengesWithTasks(groupToBelong, 3, 1, challenger);
        Task taskToHunt = challengeToQuery.getTasks().get(0);

        // when
        ExtractableResponse<?> response = with()
                .when().request(POST, "/challenges/{challengeId}/tasks/{taskId}/hunt",
                        challengeToQuery.getId(), taskToHunt.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.FORBIDDEN_CONTENT.getCode());
        assertThat(response.jsonPath().getString("message"))
                .contains("Task is not failed");
    }

    @Test
    @DisplayName("챌린지 테스크 헌팅 실패 - 선착순 경쟁에 밀림")
    void huntingTask_Fail_Competition() {
        // given
        User challenger = userSupport.registerUserToDB();
        User anotherHunter = userSupport.registerUserToDB();
        Group groupToBelong = groupSupport.callToMakeGroup(List.of(currentUser, challenger, anotherHunter));
        Challenge challengeToQuery = challengeSupport
                .callToMakeChallengeWithFailedTasks(groupToBelong, 3, challenger);
        Task taskToHunt = challengeToQuery.getTasks().get(0);
        challengeSupport.callToHuntTask(anotherHunter, challengeToQuery, taskToHunt);

        // when
        ExtractableResponse<?> response = with()
                .when().request(POST, "/challenges/{challengeId}/tasks/{taskId}/hunt",
                        challengeToQuery.getId(), taskToHunt.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.FORBIDDEN_CONTENT.getCode());
        assertThat(response.jsonPath().getString("message"))
                .contains("Hunting limit reached for this task");
    }
}

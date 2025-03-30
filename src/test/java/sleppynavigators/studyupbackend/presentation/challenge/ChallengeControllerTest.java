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
import java.util.List;
import java.util.Objects;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sleppynavigators.studyupbackend.common.RestAssuredBaseTest;
import sleppynavigators.studyupbackend.common.support.AuthSupport;
import sleppynavigators.studyupbackend.common.support.ChallengeSupport;
import sleppynavigators.studyupbackend.common.support.GroupSupport;
import sleppynavigators.studyupbackend.common.support.UserSupport;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskCertificationRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskListResponse;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskListResponse.TaskListItem;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskResponse;

@DisplayName("ChallengeController API 테스트")
public class ChallengeControllerTest extends RestAssuredBaseTest {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private AuthSupport authSupport;

    @Autowired
    private GroupSupport groupSupport;

    @Autowired
    private ChallengeSupport challengeSupport;

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
}

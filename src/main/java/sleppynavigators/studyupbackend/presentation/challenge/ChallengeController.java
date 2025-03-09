package sleppynavigators.studyupbackend.presentation.challenge;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sleppynavigators.studyupbackend.application.challenge.ChallengeService;
import sleppynavigators.studyupbackend.presentation.authentication.filter.UserPrincipal;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskCertificationRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskListResponse;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskResponse;
import sleppynavigators.studyupbackend.presentation.common.SuccessResponse;

@Tag(name = "Challenge", description = "챌린지 관련 API")
@RestController
@RequestMapping("/challenges")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping("/{challengeId}/tasks")
    @Operation(summary = "챌린지 테스크 목록 조회", description = "챌린지의 테스크 목록을 조회합니다.")
    public ResponseEntity<SuccessResponse<TaskListResponse>> getTasks(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long challengeId
    ) {
        // TODO: filter by certification status utilizing `RSQL` or `QueryDSL Web Support`
        Long userId = userPrincipal.userId();
        TaskListResponse response = challengeService.getTasks(userId, challengeId);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }

    @PostMapping("/{challengeId}/tasks/{taskId}/certify")
    @Operation(summary = "챌린지 테스크 완료", description = "챌린지의 테스크를 완료합니다.")
    public ResponseEntity<SuccessResponse<TaskResponse>> completeTask(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long challengeId,
            @PathVariable Long taskId,
            @RequestBody @Valid TaskCertificationRequest taskCertificationRequest
    ) {
        Long userId = userPrincipal.userId();
        TaskResponse response = challengeService.completeTask(userId, challengeId, taskId, taskCertificationRequest);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }
}

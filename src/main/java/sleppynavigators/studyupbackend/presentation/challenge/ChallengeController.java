package sleppynavigators.studyupbackend.presentation.challenge;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sleppynavigators.studyupbackend.presentation.authentication.filter.UserPrincipal;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.ChallengeResponse;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskListResponse;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskResponse;
import sleppynavigators.studyupbackend.presentation.common.SuccessResponse;

@Tag(name = "Challenge", description = "챌린지 관련 API")
@RestController
@RequestMapping("/challenges")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeController {

    @PostMapping
    @Operation(summary = "챌린지 생성", description = "챌린지를 생성합니다.")
    public ResponseEntity<SuccessResponse<ChallengeResponse>> createChallenge(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long userId = userPrincipal.userId();
        return ResponseEntity.ok(new SuccessResponse<>(null));
    }

    @GetMapping("/{challengeId}/tasks")
    @Operation(summary = "챌린지 과제 목록 조회", description = "챌린지의 과제 목록을 조회합니다.")
    public ResponseEntity<SuccessResponse<TaskListResponse>> getTasks(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long challengeId
    ) {
        Long userId = userPrincipal.userId();
        return ResponseEntity.ok(new SuccessResponse<>(null));
    }

    @PostMapping("/{challengeId}/tasks")
    @Operation(summary = "챌린지 과제 생성", description = "챌린지에 과제를 생성합니다.")
    public ResponseEntity<SuccessResponse<TaskResponse>> createTask(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long challengeId
    ) {
        Long userId = userPrincipal.userId();
        return ResponseEntity.ok(new SuccessResponse<>(null));
    }
}

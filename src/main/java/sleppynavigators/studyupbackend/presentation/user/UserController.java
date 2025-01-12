package sleppynavigators.studyupbackend.presentation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sleppynavigators.studyupbackend.application.user.UserService;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.presentation.common.APIResponse;
import sleppynavigators.studyupbackend.presentation.common.APIResult;
import sleppynavigators.studyupbackend.presentation.user.dto.SampleResponse;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserController {
    private final UserService userService;

    @GetMapping("/sample")
    @Operation(summary = "샘플 사용자 조회", description = "샘플 사용자를 조회합니다.")
    public APIResponse<SampleResponse> sample() {
        User queryResult = userService.sampleUser();
        SampleResponse data = SampleResponse.from(queryResult);
        return new APIResponse<>(APIResult.QUERY_OK, data);
    }
}

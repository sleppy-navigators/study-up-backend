package sleppynavigators.studyupbackend.presentation.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sleppynavigators.studyupbackend.application.user.UserService;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.presentation.common.SU_Response;
import sleppynavigators.studyupbackend.presentation.common.SU_ResponseResult;
import sleppynavigators.studyupbackend.presentation.user.dto.SampleResponse;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserController {
    private final UserService userService;

    @GetMapping("/sample")
    public SU_Response sample() {
        User queryResult = userService.sampleUser();
        SampleResponse data = SampleResponse.from(queryResult);
        return new SU_Response(SU_ResponseResult.QUERY_OK, data);
    }
}

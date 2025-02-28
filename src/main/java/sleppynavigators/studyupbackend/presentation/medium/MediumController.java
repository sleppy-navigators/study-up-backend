package sleppynavigators.studyupbackend.presentation.medium;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import java.net.URL;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sleppynavigators.studyupbackend.infrastructure.common.medium.MediumStorageClient;
import sleppynavigators.studyupbackend.presentation.authentication.filter.UserPrincipal;
import sleppynavigators.studyupbackend.presentation.common.SuccessResponse;
import sleppynavigators.studyupbackend.presentation.medium.dto.response.UploadUrlResponse;

@Tag(name = "Medium", description = "미디어 관련 API")
@RestController
@RequestMapping("/media")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MediumController {

    private final MediumStorageClient mediumStorageClient;

    @PostMapping("/upload-url")
    @Operation(summary = "S3 pre-signed URL 발급", description = "S3 pre-signed URL을 발급합니다.")
    public ResponseEntity<SuccessResponse<UploadUrlResponse>> getPreSignedUploadUrl(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam @NotBlank String filename) {
        URL uploadUrl = mediumStorageClient.getUploadUrl(userPrincipal.userId(), filename);
        UploadUrlResponse response = new UploadUrlResponse(uploadUrl);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }
}

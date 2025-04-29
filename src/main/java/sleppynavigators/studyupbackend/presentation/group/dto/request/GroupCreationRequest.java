package sleppynavigators.studyupbackend.presentation.group.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.business.InvalidPayloadException;

@Schema(description = "그룹 생성 요청")
public record GroupCreationRequest(
        @Schema(description = "그룹 이름", example = "웹 마스터")
        @NotBlank String name,

        @Schema(description = "그룹 설명", example = "아무튼 웹을 마스터하기 위한 그룹")
        @NotBlank String description,

        @Schema(description = "그룹 썸네일 URL", example = "https://example.com/thumbnail.jpg")
        String thumbnailUrl) {

    public Group toEntity(User creator) {
        try {
            return Group.builder()
                    .name(name)
                    .description(description)
                    .thumbnailUrl(thumbnailUrl)
                    .creator(creator)
                    .build();
        } catch (IllegalArgumentException ex) {
            throw new InvalidPayloadException(ex);
        }
    }
}

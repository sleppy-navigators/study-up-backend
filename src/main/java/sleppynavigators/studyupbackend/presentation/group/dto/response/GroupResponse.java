package sleppynavigators.studyupbackend.presentation.group.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.group.vo.GroupDetail;

@Schema(description = "그룹 정보 응답")
public record GroupResponse(
        @Schema(description = "그룹 ID", example = "1")
        @NotNull Long id,

        @Schema(description = "그룹 이름", example = "스터디 그룹")
        @NotBlank String name,

        @Schema(description = "그룹 설명", example = "스터디 그룹 설명")
        @NotBlank String description,

        @Schema(description = "그룹 썸네일 URL", example = "https://example.com/thumbnail.jpg")
        String thumbnailUrl) {

    public static GroupResponse fromEntity(Group group) {
        GroupDetail groupDetail = group.getGroupDetail();
        return new GroupResponse(
                group.getId(),
                groupDetail.getName(),
                groupDetail.getDescription(),
                groupDetail.getThumbnailUrl());
    }
}

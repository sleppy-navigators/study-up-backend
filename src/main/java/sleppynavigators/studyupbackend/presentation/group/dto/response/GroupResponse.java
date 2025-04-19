package sleppynavigators.studyupbackend.presentation.group.dto.response;

import jakarta.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.group.vo.GroupDetail;

public record GroupResponse(@NotNull Long id,
                            @NotBlank String name,
                            @NotBlank String description,
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

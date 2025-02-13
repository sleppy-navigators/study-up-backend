package sleppynavigators.studyupbackend.presentation.group.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.group.vo.GroupInfo;

public record SimpleGroupResponse(@NotNull Long id,
                                  @NotBlank String name,
                                  @NotBlank String description,
                                  @Email String thumbnailUrl) {

    public static SimpleGroupResponse fromEntity(Group group) {
        GroupInfo groupInfo = group.getGroupInfo();
        return new SimpleGroupResponse(
                group.getId(), groupInfo.name(), groupInfo.description(), groupInfo.thumbnailUrl());
    }
}

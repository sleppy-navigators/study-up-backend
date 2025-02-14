package sleppynavigators.studyupbackend.presentation.group.dto.response;

import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.group.vo.GroupDetail;

public record SimpleGroupResponse(Long id, String name, String description, String thumbnailUrl) {

    public static SimpleGroupResponse fromEntity(Group group) {
        GroupDetail groupDetail = group.getGroupDetail();
        return new SimpleGroupResponse(
                group.getId(), groupDetail.name(), groupDetail.description(), groupDetail.thumbnailUrl());
    }
}

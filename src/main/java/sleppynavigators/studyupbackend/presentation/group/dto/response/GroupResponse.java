package sleppynavigators.studyupbackend.presentation.group.dto.response;

import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.group.vo.GroupDetail;

public record GroupResponse(Long id, String name, String description, String thumbnailUrl) {

    public static GroupResponse fromEntity(Group group) {
        GroupDetail groupDetail = group.getGroupDetail();
        return new GroupResponse(
                group.getId(), groupDetail.name(), groupDetail.description(), groupDetail.thumbnailUrl());
    }
}

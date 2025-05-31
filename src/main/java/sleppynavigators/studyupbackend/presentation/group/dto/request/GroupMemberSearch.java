package sleppynavigators.studyupbackend.presentation.group.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springdoc.core.annotations.ParameterObject;
import sleppynavigators.studyupbackend.application.group.GroupMemberSortType;

@ParameterObject
@Schema(description = "그룹 멤버 검색 조건")
public record GroupMemberSearch(
        @Schema(description = "정렬 조건", example = "POINT")
        GroupMemberSortType sortBy
) {

    private static final GroupMemberSortType DEFAULT_SORT_BY = GroupMemberSortType.NONE;

    public GroupMemberSearch {
        if (sortBy == null) {
            sortBy = DEFAULT_SORT_BY;
        }
    }
}

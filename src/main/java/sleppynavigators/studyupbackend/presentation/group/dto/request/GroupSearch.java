package sleppynavigators.studyupbackend.presentation.group.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import sleppynavigators.studyupbackend.application.group.GroupSortType;

@Schema(description = "그룹 검색 조건")
public record GroupSearch(
        @Schema(description = "정렬 조건", example = "LATEST_CHAT")
        GroupSortType sortBy) {

    private static final GroupSortType DEFAULT_SORT_BY = GroupSortType.NONE;

    public GroupSearch {
        if (sortBy == null) {
            sortBy = DEFAULT_SORT_BY;
        }
    }
}

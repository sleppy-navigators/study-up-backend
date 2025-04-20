package sleppynavigators.studyupbackend.presentation.group.dto.request;

import sleppynavigators.studyupbackend.application.group.GroupSortType;

public record GroupSearch(GroupSortType sortBy) {

    private static final GroupSortType DEFAULT_SORT_BY = GroupSortType.NONE;

    public GroupSearch {
        if (sortBy == null) {
            sortBy = DEFAULT_SORT_BY;
        }
    }
}

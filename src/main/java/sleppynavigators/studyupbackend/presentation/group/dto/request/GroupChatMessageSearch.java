package sleppynavigators.studyupbackend.presentation.group.dto.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record GroupChatMessageSearch(
        Integer pageNum,
        Integer pageSize,
        GroupChatMessageSortType sortBy
) {

    public enum GroupChatMessageSortType {
        LATEST,
        NONE,
    }

    private static final int DEFAULT_PAGE_NUM = 0;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final GroupChatMessageSortType DEFAULT_SORT_BY = GroupChatMessageSortType.NONE;

    public GroupChatMessageSearch {
        if (pageNum == null) {
            pageNum = DEFAULT_PAGE_NUM;
        }
        if (pageSize == null) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        if (sortBy == null) {
            sortBy = DEFAULT_SORT_BY;
        }
    }

    public Pageable toPageable() {
        return PageRequest.of(pageNum, pageSize, toSort());
    }

    private Sort toSort() {
        return switch (sortBy) {
            case LATEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case NONE -> Sort.unsorted();
        };
    }
}

package sleppynavigators.studyupbackend.presentation.chat.dto.request;

import org.hibernate.validator.constraints.Range;

public record ChatMessageSearch(
        @Range Long pageNum,
        @Range Integer pageSize
) {

    private static final long DEFAULT_PAGE_NUM = 0L;
    private static final int DEFAULT_PAGE_SIZE = 20;

    public ChatMessageSearch {
        if (pageNum == null) {
            pageNum = DEFAULT_PAGE_NUM;
        }
        if (pageSize == null) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
    }
}

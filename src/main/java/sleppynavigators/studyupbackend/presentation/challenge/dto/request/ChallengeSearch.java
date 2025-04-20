package sleppynavigators.studyupbackend.presentation.challenge.dto.request;

import org.hibernate.validator.constraints.Range;
import sleppynavigators.studyupbackend.application.challenge.ChallengeSortType;

public record ChallengeSearch(
        @Range Long pageNum,
        @Range Integer pageSize,
        ChallengeSortType sortBy
) {

    private static final long DEFAULT_PAGE_NUM = 0L;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final ChallengeSortType DEFAULT_SORT_BY = ChallengeSortType.NONE;

    public ChallengeSearch {
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
}

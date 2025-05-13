package sleppynavigators.studyupbackend.presentation.challenge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.Range;
import org.springdoc.core.annotations.ParameterObject;
import sleppynavigators.studyupbackend.application.challenge.ChallengeSortType;

@ParameterObject
@Schema(description = "챌린지 검색 조건")
public record ChallengeSearch(
        @Schema(description = "페이지 번호", example = "0")
        @Range Long pageNum,

        @Schema(description = "페이지 크기", example = "20")
        @Range Integer pageSize,

        @Schema(description = "정렬 조건", example = "LATEST_CERTIFICATION")
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

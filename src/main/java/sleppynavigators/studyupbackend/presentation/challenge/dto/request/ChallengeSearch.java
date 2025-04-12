package sleppynavigators.studyupbackend.presentation.challenge.dto.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;

public record ChallengeSearch(
        Integer pageNum,
        Integer pageSize,
        ChallengeSortType sortBy
) {

    public enum ChallengeSortType {
        LATEST,
        NONE,
    }

    private static final int DEFAULT_PAGE_NUM = 0;
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

    public Pageable toPageable() {
        return PageRequest.of(pageNum, pageSize, toSort());
    }

    private Sort toSort() {
        return switch (sortBy) {
            case LATEST, NONE -> Sort.unsorted();
        };
    }

    public Specification<Challenge> toSpecification() {
        return (root, query, criteriaBuilder) -> {
            assert query != null;

            if (sortBy == ChallengeSortType.LATEST) {
                query.groupBy(root.get("id"));
                query.orderBy(criteriaBuilder.desc(
                        criteriaBuilder.max(root.join("tasks")
                                .get("certification")
                                .get("certifiedAt"))));
            }

            return criteriaBuilder.conjunction();
        };
    }
}

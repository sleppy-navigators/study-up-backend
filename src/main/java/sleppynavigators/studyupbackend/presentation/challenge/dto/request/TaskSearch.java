package sleppynavigators.studyupbackend.presentation.challenge.dto.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import sleppynavigators.studyupbackend.domain.challenge.Task;

import java.time.LocalDateTime;

public record TaskSearch(
        Integer pageNum,
        Integer pageSize,
        CertificationStatus status
) {

    public enum CertificationStatus {
        SUCCEED,
        FAILED,
        IN_PROGRESS,
        COMPLETED,
        ALL,
    }

    private static final int DEFAULT_PAGE_NUM = 0;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final CertificationStatus DEFAULT_CERTIFICATION_STATUS = CertificationStatus.ALL;

    public TaskSearch {
        if (pageNum == null) {
            pageNum = DEFAULT_PAGE_NUM;
        }
        if (pageSize == null) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        if (status == null) {
            status = DEFAULT_CERTIFICATION_STATUS;
        }
    }

    public Pageable toPageable() {
        return PageRequest.of(pageNum, pageSize, Sort.unsorted());
    }

    public Specification<Task> toSpecification() {
        return (root, query, criteriaBuilder) ->
                switch (status) {
                    case SUCCEED -> criteriaBuilder.isNotNull(root.get("certification").get("certifiedAt"));
                    case FAILED -> criteriaBuilder.and(
                            criteriaBuilder.isNull(root.get("certification").get("certifiedAt")),
                            criteriaBuilder.lessThanOrEqualTo(root.get("detail").get("deadline"), LocalDateTime.now()));
                    case IN_PROGRESS -> criteriaBuilder.and(
                            criteriaBuilder.isNull(root.get("certification").get("certifiedAt")),
                            criteriaBuilder.greaterThan(root.get("detail").get("deadline"), LocalDateTime.now()));
                    case COMPLETED -> criteriaBuilder.or(
                            criteriaBuilder.isNotNull(root.get("certification").get("certifiedAt")),
                            criteriaBuilder.lessThanOrEqualTo(root.get("detail").get("deadline"), LocalDateTime.now()));
                    case ALL -> criteriaBuilder.conjunction();
                };
    }
}

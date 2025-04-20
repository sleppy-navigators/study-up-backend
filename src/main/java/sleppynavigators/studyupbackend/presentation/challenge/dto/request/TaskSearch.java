package sleppynavigators.studyupbackend.presentation.challenge.dto.request;

import org.hibernate.validator.constraints.Range;
import sleppynavigators.studyupbackend.application.challenge.TaskCertificationStatus;

public record TaskSearch(
        @Range Long pageNum,
        @Range Integer pageSize,
        TaskCertificationStatus status
) {

    private static final long DEFAULT_PAGE_NUM = 0L;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final TaskCertificationStatus DEFAULT_CERTIFICATION_STATUS = TaskCertificationStatus.ALL;

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
}

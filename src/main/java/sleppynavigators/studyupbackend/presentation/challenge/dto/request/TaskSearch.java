package sleppynavigators.studyupbackend.presentation.challenge.dto.request;

public record TaskSearch(
        Integer pageNum,
        Integer pageSize,
        TaskCertificationStatus status
) {

    public enum TaskCertificationStatus {
        SUCCEED,
        FAILED,
        IN_PROGRESS,
        COMPLETED,
        ALL,
    }

    private static final int DEFAULT_PAGE_NUM = 0;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final TaskCertificationStatus DEFAULT_TASK_CERTIFICATION_STATUS = TaskCertificationStatus.ALL;

    public TaskSearch {
        if (pageNum == null) {
            pageNum = DEFAULT_PAGE_NUM;
        }
        if (pageSize == null) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        if (status == null) {
            status = DEFAULT_TASK_CERTIFICATION_STATUS;
        }
    }
}

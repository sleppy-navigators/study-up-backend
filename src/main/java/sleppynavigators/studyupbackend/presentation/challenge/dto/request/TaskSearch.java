package sleppynavigators.studyupbackend.presentation.challenge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.Range;
import org.springdoc.core.annotations.ParameterObject;
import sleppynavigators.studyupbackend.application.challenge.TaskCertificationStatus;

@ParameterObject
@Schema(description = "과제 검색 조건")
public record TaskSearch(
        @Schema(description = "페이지 번호", example = "0")
        @Range Long pageNum,

        @Schema(description = "페이지 크기", example = "20")
        @Range Integer pageSize,

        @Schema(description = "인증 상태", example = "ALL")
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

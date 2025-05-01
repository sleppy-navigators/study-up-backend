package sleppynavigators.studyupbackend.presentation.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.Range;

@Schema(description = "채팅 메시지 검색 조건")
public record ChatMessageSearch(
        @Schema(description = "페이지 번호", example = "0")
        @Range Long pageNum,

        @Schema(description = "페이지 크기", example = "20")
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

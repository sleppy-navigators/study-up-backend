package sleppynavigators.studyupbackend.presentation.common;

import lombok.Getter;

/**
 * API 응답 결과를 나타내는 Enum 클래스
 *
 * @version 1.0
 * @see APIResponse
 */
@Getter
public enum APIResult {

    QUERY_OK("S20", "Query Success"),

    QUERY_NOTFOUND("F44", "Query Fail"),
    BAD_REQUEST("F40", "Query Fail"),
    UNAUTHORIZED("F41", "Authentication Fail"),
    FORBIDDEN("F43", "Authorization Fail"),

    INTERNAL_SERVER_ERROR("F50", "Retry Later");

    /**
     * Frontend 개발자에게 전달할 구체적인 에러 코드
     * <p>일반 사용자는 해당 코드를 이해할 수 없지만, Frontend 개발자는 디버깅에 활용할 수 있음</p>
     */
    private final String code;

    /**
     * 서비스 사용자에게 전달할 에러 메시지
     * <p>일반 사용자에게 노출할 수 있기 때문에, 구체적인 오류 내용이 드러나면 안 됨</p>
     */
    private final String message;

    APIResult(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

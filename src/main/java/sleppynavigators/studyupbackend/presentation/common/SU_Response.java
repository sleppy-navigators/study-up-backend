package sleppynavigators.studyupbackend.presentation.common;

public record SU_Response(SU_ResponseResult responseResult, Object data) {
    public SU_Response(SU_ResponseResult responseResult) {
        this(responseResult, null);
    }
}

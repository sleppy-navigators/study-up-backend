package sleppynavigators.studyupbackend.presentation.common;

public record SU_Response<T>(SU_ResponseResult responseResult, T data) {
    public SU_Response(SU_ResponseResult responseResult) {
        this(responseResult, null);
    }
}

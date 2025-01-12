package sleppynavigators.studyupbackend.presentation.common;

public record APIResponse<T>(APIResult apiResult, T data) {
    public APIResponse(APIResult apiResult) {
        this(apiResult, null);
    }
}

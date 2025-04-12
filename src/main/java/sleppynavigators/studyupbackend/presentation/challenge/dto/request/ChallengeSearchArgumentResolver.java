package sleppynavigators.studyupbackend.presentation.challenge.dto.request;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import sleppynavigators.studyupbackend.exception.network.InvalidApiException;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.ChallengeSearch.ChallengeSortType;
import sleppynavigators.studyupbackend.presentation.common.SearchParam;

public class ChallengeSearchArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return (parameter.hasParameterAnnotation(SearchParam.class) &&
                parameter.nestedIfOptional().getParameterType().equals(ChallengeSearch.class));
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        try {
            String pageNum = webRequest.getParameter("pageNum");
            String pageSize = webRequest.getParameter("pageSize");
            String sortType = webRequest.getParameter("sortBy");

            return new ChallengeSearch(
                    pageNum != null ? Integer.parseInt(pageNum) : null,
                    pageSize != null ? Integer.parseInt(pageSize) : null,
                    sortType != null ? ChallengeSortType.valueOf(sortType) : null);
        } catch (IllegalArgumentException e) {
            throw new InvalidApiException("Invalid search option provided");
        }
    }
}

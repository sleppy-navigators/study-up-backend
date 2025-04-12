package sleppynavigators.studyupbackend.presentation.group.dto.request;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import sleppynavigators.studyupbackend.exception.network.InvalidApiException;
import sleppynavigators.studyupbackend.presentation.common.SearchParam;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupSearch.GroupSortType;

public class GroupSearchArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return (parameter.hasParameterAnnotation(SearchParam.class) &&
                parameter.nestedIfOptional().getParameterType().equals(GroupSearch.class));

    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        try {
            String sortType = webRequest.getParameter("sortBy");
            return new GroupSearch(sortType != null ? GroupSortType.valueOf(sortType) : null);
        } catch (IllegalArgumentException e) {
            throw new InvalidApiException("Invalid search option provided");
        }
    }
}

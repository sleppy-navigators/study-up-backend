package sleppynavigators.studyupbackend.presentation.group.dto.request;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import sleppynavigators.studyupbackend.presentation.common.SearchParam;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupSearch.GroupSortType;

import java.util.Optional;

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
            GroupSortType sortType = Optional.ofNullable(webRequest.getParameter("sortBy"))
                    .map(GroupSortType::valueOf)
                    .orElse(null);

            return new GroupSearch(sortType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid search option provided");
        }
    }
}

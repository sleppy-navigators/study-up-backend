package sleppynavigators.studyupbackend.presentation.challenge.dto.request;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskSearch.TaskCertificationStatus;
import sleppynavigators.studyupbackend.presentation.common.SearchParam;

import java.util.Optional;

public class TaskSearchArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return (parameter.hasParameterAnnotation(SearchParam.class) &&
                parameter.nestedIfOptional().getParameterType().equals(TaskSearch.class));
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Integer pageNum = Optional.ofNullable(webRequest.getParameter("pageNum"))
                .map(Integer::parseInt)
                .orElse(null);
        Integer pageSize = Optional.ofNullable(webRequest.getParameter("pageSize"))
                .map(Integer::parseInt)
                .orElse(null);
        TaskCertificationStatus taskCertificationStatus = Optional.ofNullable(
                        webRequest.getParameter("status"))
                .map(TaskCertificationStatus::valueOf)
                .orElse(null);

        return new TaskSearch(pageNum, pageSize, taskCertificationStatus);
    }
}

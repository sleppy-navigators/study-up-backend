package sleppynavigators.studyupbackend.presentation.common.argument;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import sleppynavigators.studyupbackend.application.challenge.TaskCertificationStatus;
import sleppynavigators.studyupbackend.exception.network.InvalidApiException;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskSearch;

public class TaskSearchArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return (parameter.hasParameterAnnotation(SearchParam.class) &&
                parameter.nestedIfOptional().getParameterType().equals(TaskSearch.class));
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        try {
            String pageNum = webRequest.getParameter("pageNum");
            String pageSize = webRequest.getParameter("pageSize");
            String certificationStatus = webRequest.getParameter("status");

            return new TaskSearch(
                    pageNum != null ? Long.parseLong(pageNum) : null,
                    pageSize != null ? Integer.parseInt(pageSize) : null,
                    certificationStatus != null ? TaskCertificationStatus.valueOf(certificationStatus) : null);
        } catch (IllegalArgumentException e) {
            throw new InvalidApiException("Invalid search option provided");
        }
    }
}

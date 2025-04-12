package sleppynavigators.studyupbackend.presentation.chat.dto.request;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import sleppynavigators.studyupbackend.exception.network.InvalidApiException;
import sleppynavigators.studyupbackend.presentation.common.SearchParam;
import sleppynavigators.studyupbackend.presentation.chat.dto.request.ChatMessageSearch.GroupChatMessageSortType;

public class ChatMessageSearchArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return (parameter.hasParameterAnnotation(SearchParam.class) &&
                parameter.nestedIfOptional().getParameterType().equals(ChatMessageSearch.class));
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        try {
            String pageNum = webRequest.getParameter("pageNum");
            String pageSize = webRequest.getParameter("pageSize");
            String sortBy = webRequest.getParameter("sortBy");

            return new ChatMessageSearch(
                    pageNum != null ? Integer.parseInt(pageNum) : null,
                    pageSize != null ? Integer.parseInt(pageSize) : null,
                    sortBy != null ? GroupChatMessageSortType.valueOf(sortBy) : null);
        } catch (IllegalArgumentException e) {
            throw new InvalidApiException("Invalid search option provided");
        }
    }
}

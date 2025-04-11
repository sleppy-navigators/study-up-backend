package sleppynavigators.studyupbackend.presentation.chat.dto.request;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import sleppynavigators.studyupbackend.presentation.common.SearchParam;
import sleppynavigators.studyupbackend.presentation.chat.dto.request.ChatMessageSearch.GroupChatMessageSortType;

import java.util.Optional;

public class ChatMessageSearchArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return (parameter.hasParameterAnnotation(SearchParam.class) &&
                parameter.nestedIfOptional().getParameterType().equals(ChatMessageSearch.class));
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Integer pageNum = Optional.ofNullable(webRequest.getParameter("pageNum"))
                .map(Integer::parseInt)
                .orElse(null);
        Integer pageSize = Optional.ofNullable(webRequest.getParameter("pageSize"))
                .map(Integer::parseInt)
                .orElse(null);
        GroupChatMessageSortType sortBy = Optional.ofNullable(webRequest.getParameter("sortBy"))
                .map(GroupChatMessageSortType::valueOf)
                .orElse(null);

        return new ChatMessageSearch(pageNum, pageSize, sortBy);
    }
}

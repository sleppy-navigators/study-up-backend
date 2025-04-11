package sleppynavigators.studyupbackend.presentation.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskSearchArgumentResolver;
import sleppynavigators.studyupbackend.presentation.chat.dto.request.ChatMessageSearchArgumentResolver;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.addAll(
                List.of(
                        new ChatMessageSearchArgumentResolver(),
                        new TaskSearchArgumentResolver()));
    }
}

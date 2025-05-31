package sleppynavigators.studyupbackend.presentation.common.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import sleppynavigators.studyupbackend.presentation.common.argument.ChallengeSearchArgumentResolver;
import sleppynavigators.studyupbackend.presentation.common.argument.ChatMessageSearchArgumentResolver;
import sleppynavigators.studyupbackend.presentation.common.argument.GroupMemberSearchArgumentResolver;
import sleppynavigators.studyupbackend.presentation.common.argument.GroupSearchArgumentResolver;
import sleppynavigators.studyupbackend.presentation.common.argument.TaskSearchArgumentResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.addAll(
                List.of(
                        new ChatMessageSearchArgumentResolver(),
                        new TaskSearchArgumentResolver(),
                        new ChallengeSearchArgumentResolver(),
                        new GroupSearchArgumentResolver(),
                        new GroupMemberSearchArgumentResolver()));
    }
}

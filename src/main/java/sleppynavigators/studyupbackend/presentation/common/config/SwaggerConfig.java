package sleppynavigators.studyupbackend.presentation.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.customizers.ParameterCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sleppynavigators.studyupbackend.presentation.common.PublicAPI;
import sleppynavigators.studyupbackend.presentation.common.argument.SearchParam;

@Configuration
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SwaggerConfig {

    private final String SECURITY_KEY_BEARER = "bearer-key";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .components(securitySchemeComponents())
                .addSecurityItem(securityRequirement())
                .servers(servers());
    }

    @Bean
    public OperationCustomizer customGlobalHeaderOperation() {
        return (operation, handlerMethod) -> {
            if (handlerMethod.getMethodAnnotation(PublicAPI.class) != null) {
                operation.setSecurity(Collections.emptyList());
            }
            return operation;
        };
    }

    @Bean
    public ParameterCustomizer searchParamCustomizer() {
        return (parameterBuilder, methodParameter) -> {
            if (methodParameter.hasParameterAnnotation(SearchParam.class)) {
                parameterBuilder.required(false);
            }
            return parameterBuilder;
        };
    }

    private Info apiInfo() {
        return new Info()
                .title("StudyUp API")
                .description("StudyUp API Documentation")
                .version("1.0.0");
    }

    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList(SECURITY_KEY_BEARER);
    }

    private Components securitySchemeComponents() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .in(SecurityScheme.In.HEADER)
                .scheme("bearer")
                .bearerFormat("JWT");
        return new Components().addSecuritySchemes(SECURITY_KEY_BEARER, securityScheme);
    }

    private List<Server> servers() {
        Server productionServer = new Server().url("https://api.study-up.site").description("Production server");
        Server localServer = new Server().url("http://localhost:8080").description("Local server");

        return List.of(productionServer, localServer);
    }
}

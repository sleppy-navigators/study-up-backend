package sleppynavigators.studyupbackend.presentation.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .components(securitySchemeComponents())
                .addSecurityItem(securityRequirement())
                .servers(servers());
    }

    private Info apiInfo() {
        return new Info()
                .title("StudyUp API")
                .description("StudyUp API Documentation")
                .version("1.0.0");
    }

    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList("bearer-key");
    }

    private Components securitySchemeComponents() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .in(SecurityScheme.In.HEADER)
                .scheme("bearer")
                .bearerFormat("JWT");
        return new Components().addSecuritySchemes("bearer-key", securityScheme);
    }

    private List<Server> servers() {
        Server localServer = new Server().url("http://localhost:8080").description("Local server");

        // TODO: change the URL of the staging server to the actual URL of the staging server.
        Server stagingServer = new Server().url("https://whitepiano-codeserver.pe.kr").description("Staging server");

        return List.of(localServer, stagingServer);
    }
}

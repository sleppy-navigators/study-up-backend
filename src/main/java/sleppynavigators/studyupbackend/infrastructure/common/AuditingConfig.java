package sleppynavigators.studyupbackend.infrastructure.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableJpaAuditing
@EnableMongoAuditing
public class AuditingConfig {

    @Bean
    public AuditorAware<?> auditorProvider() {
        return new UserIdAuditorAware();
    }
}

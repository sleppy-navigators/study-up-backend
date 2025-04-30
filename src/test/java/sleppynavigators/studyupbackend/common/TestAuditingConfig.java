package sleppynavigators.studyupbackend.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import java.util.Optional;

@Profile("test")
@Configuration
public class TestAuditingConfig {

    @Bean
    public AuditorAware<Long> testAuditorProvider() {
        return () -> Optional.of(1L);
    }
}

package sleppynavigators.studyupbackend.common;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;

@Profile("test")
@Configuration
public class TestAuditingConfig {

    @Bean
    @Primary
    public AuditorAware<Long> testAuditorProvider() {
        return () -> Optional.of(1L);
    }
}

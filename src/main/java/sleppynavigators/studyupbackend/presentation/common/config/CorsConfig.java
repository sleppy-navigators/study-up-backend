package sleppynavigators.studyupbackend.presentation.common.config;

import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Cross-Origin Resource Sharing Configuration
 * <p>
 * As our service is aimed at mobile platforms, this is likely to be a setup for <b>development environments only</b>.
 */

@Configuration
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CorsConfig {

    private final List<String> allowedOrigins = List.of("*");
    private final List<String> allowedMethods = Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS");
    private final List<String> allowedHeaders = Arrays.asList(
            "Origin", "X-Requested-With", "Content-Type", "Accept", "Authorization", "Location", "Range",
            "Cache-Control", "User-Agent", "DNT");

    //@Primary
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(allowedOrigins);
        corsConfiguration.setAllowedMethods(allowedMethods);
        corsConfiguration.setAllowedHeaders(allowedHeaders);
        corsConfiguration.setAllowCredentials(true);
        return request -> corsConfiguration;
    }
}

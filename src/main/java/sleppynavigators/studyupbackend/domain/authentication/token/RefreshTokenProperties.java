package sleppynavigators.studyupbackend.domain.authentication.token;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "authentication.refresh-token")
public record RefreshTokenProperties(Long expirationInMinutes) {
    public RefreshTokenProperties {
        validateExpirationInMinutes(expirationInMinutes);
    }

    private void validateExpirationInMinutes(Long expirationInMinutes) {
        if (expirationInMinutes == null || expirationInMinutes <= 0) {
            throw new IllegalArgumentException("Expiration in minutes must be greater than 0");
        }
    }
}

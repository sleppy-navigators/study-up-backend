package sleppynavigators.studyupbackend.domain.authentication.token;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "authentication.access-token")
public record AccessTokenProperties(String secret, Long expirationInMilliseconds) {
    public AccessTokenProperties {
        validateSecret(secret);
        validateExpirationInMilliseconds(expirationInMilliseconds);
    }

    private void validateSecret(String secret) {
        if (StringUtils.isBlank(secret)) {
            throw new IllegalArgumentException("Secret must not be null or empty");
        }
    }

    private void validateExpirationInMilliseconds(Long expirationInMilliseconds) {
        if (expirationInMilliseconds == null || expirationInMilliseconds <= 0) {
            throw new IllegalArgumentException("Expiration in milliseconds must be greater than 0");
        }
    }
}

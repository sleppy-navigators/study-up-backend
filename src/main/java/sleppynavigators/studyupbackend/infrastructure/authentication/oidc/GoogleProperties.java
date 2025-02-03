package sleppynavigators.studyupbackend.infrastructure.authentication.oidc;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "authentication.oidc.google")
public record GoogleProperties(String certificateUrl, String issuer, String audience) {

    public GoogleProperties {
        validateCertificateUrl(certificateUrl);
        validateIssuer(issuer);
        validateAudience(audience);
    }

    private void validateCertificateUrl(String certificateUrl) {
        if (StringUtils.isBlank(certificateUrl)) {
            throw new IllegalArgumentException("Certificate URL must not be null or empty");
        }
    }

    private void validateIssuer(String issuer) {
        if (StringUtils.isBlank(issuer)) {
            throw new IllegalArgumentException("Issuer must not be null or empty");
        }
    }

    private void validateAudience(String audience) {
        if (StringUtils.isBlank(audience)) {
            throw new IllegalArgumentException("Audience must not be null or empty");
        }
    }
}

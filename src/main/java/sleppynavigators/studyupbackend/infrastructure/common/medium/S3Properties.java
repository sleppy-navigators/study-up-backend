package sleppynavigators.studyupbackend.infrastructure.common.medium;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.cloud.aws.s3")
public record S3Properties(String bucket, Long expirationInMinutes) {

    public S3Properties {
        validateBucket(bucket);
        validateExpirationInMinutes(expirationInMinutes);
    }

    private void validateBucket(String bucket) {
        if (StringUtils.isBlank(bucket)) {
            throw new IllegalArgumentException("Bucket must not be null or empty");
        }
    }

    private void validateExpirationInMinutes(Long expirationInMinutes) {
        if (expirationInMinutes == null || expirationInMinutes <= 0) {
            throw new IllegalArgumentException("Expiration in minutes must be greater than 0");
        }
    }
}

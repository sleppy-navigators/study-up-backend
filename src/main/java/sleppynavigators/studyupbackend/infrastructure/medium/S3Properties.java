package sleppynavigators.studyupbackend.infrastructure.medium;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.cloud.aws.s3")
public record S3Properties(String region, String bucket, Long expirationInMinutes) {

    public S3Properties {
        validateRegion(region);
        validateBucket(bucket);
        validateExpirationInMinutes(expirationInMinutes);
    }

    private void validateRegion(String region) {
        if (StringUtils.isBlank(region)) {
            throw new IllegalArgumentException("Region must not be null or empty");
        }
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

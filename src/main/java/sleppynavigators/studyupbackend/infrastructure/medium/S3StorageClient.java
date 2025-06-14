package sleppynavigators.studyupbackend.infrastructure.medium;

import io.awspring.cloud.s3.S3Template;
import java.net.URL;
import java.time.Duration;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.exception.business.InvalidPayloadException;
import sleppynavigators.studyupbackend.exception.client.UnsuccessfulResponseException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.TaggingDirective;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class S3StorageClient implements MediumStorageClient {

    private static final String S3_KEY_PATTERN = "%s/%s";
    private static final String S3_HOST_SUFFIX = ".s3";
    private static final String AWS_HOST_SUFFIX = ".amazonaws.com";

    private final S3Client s3Client;
    private final S3Template s3Template;
    private final S3Properties s3Properties;

    @Override
    public URL getUploadUrl(Long userId, String filename) {
        String key = generateKey(userId, filename);
        String bucketName = s3Properties.bucket();
        Duration expirationTime = Duration.ofMinutes(s3Properties.expirationInMinutes());

        log.info("Creating upload URL - bucket: {}, key: {}, expires in: {}",
                bucketName, key, expirationTime);
        return s3Template.createSignedPutURL(s3Properties.bucket(), key, expirationTime);
    }

    @Override
    public void updateMediaTag(URL mediaUrl, String tagging) {
        String objectKey = getObjectKey(mediaUrl);
        CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                .sourceBucket(s3Properties.bucket())
                .sourceKey(objectKey)
                .destinationBucket(s3Properties.bucket())
                .destinationKey(objectKey)
                .taggingDirective(TaggingDirective.REPLACE)
                .tagging(tagging)
                .build();

        CopyObjectResponse response = s3Client.copyObject(copyObjectRequest);
        if (response.sdkHttpResponse().isSuccessful()) {
            log.info("Successfully tag media: object={}, tagging={}", objectKey, tagging);
        } else {
            log.error("Failed to tag media: object={}, response={}", objectKey, response);
            throw new UnsuccessfulResponseException("Failed to update media tag - object:" + objectKey);
        }
    }

    @Override
    public boolean isManagedByUs(URL mediaUrl) {
        if (mediaUrl == null || mediaUrl.getHost() == null) {
            return false;
        }

        String host = mediaUrl.getHost();
        return host.equals(s3Properties.bucket() + S3_HOST_SUFFIX + AWS_HOST_SUFFIX)
                || host.equals(s3Properties.bucket() + S3_HOST_SUFFIX + "." + s3Properties.region() + AWS_HOST_SUFFIX);
    }

    private String generateKey(Long userId, String filename) {
        return String.format(S3_KEY_PATTERN, userId, filename);
    }

    private String getObjectKey(URL mediaUrl) {
        String path = mediaUrl.getPath();
        if (path == null || path.isEmpty()) {
            throw new InvalidPayloadException("Invalid media URL: " + mediaUrl);
        }

        // Remove leading slash if present
        return path.startsWith("/") ? path.substring(1) : path;
    }
}

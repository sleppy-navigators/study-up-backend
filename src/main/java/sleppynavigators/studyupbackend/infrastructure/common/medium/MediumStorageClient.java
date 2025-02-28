package sleppynavigators.studyupbackend.infrastructure.common.medium;

import io.awspring.cloud.s3.S3Template;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MediumStorageClient {

    private final S3Template s3Template;
    private final S3Properties s3Properties;

    public URL getUploadUrl(Long userId, String filename) {
        String key = userId + "/" + LocalDateTime.now() + "-" + filename;
        String bucketName = s3Properties.bucket();
        Duration expirationTime = Duration.ofMinutes(s3Properties.expirationInMinutes());

        log.info("Creating signed URL for S3 bucket: {}, key: {}, expires in: {}", bucketName, key, expirationTime);
        return s3Template.createSignedPutURL(s3Properties.bucket(), key, expirationTime);
    }
}

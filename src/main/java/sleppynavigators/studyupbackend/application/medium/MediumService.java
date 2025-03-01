package sleppynavigators.studyupbackend.application.medium;

import java.net.URL;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sleppynavigators.studyupbackend.infrastructure.medium.MediumStorageClient;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MediumService {

    private final MediumStorageClient mediumStorageClient;

    public URL getUploadUrl(Long userId, String filename) {
        return mediumStorageClient.getUploadUrl(userId, filename);
    }
}

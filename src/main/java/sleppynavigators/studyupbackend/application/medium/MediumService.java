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

    // TODO: implement a batch method to make multiple media permanent at once
    public void storeMedia(URL mediaUrl) {
        // There's no need to store/update media if it's not managed by us
        if (!mediumStorageClient.isManagedByUs(mediaUrl)) {
            return;
        }

        mediumStorageClient.updateMediaTag(mediaUrl, "status=" + MediaStatus.PERMANENT);
    }
}

package sleppynavigators.studyupbackend.domain.challenge.vo;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

@Embeddable
public record TaskCertification(@ElementCollection List<URL> externalLinks,
                                @ElementCollection List<URL> imageUrls,
                                @Column LocalDateTime certificateAt) {

    public TaskCertification {
        if ((externalLinks == null || externalLinks.isEmpty()) &&
                (imageUrls == null || imageUrls.isEmpty())) {
            throw new IllegalArgumentException("At least one external link or image URL must be provided");
        }
        if (certificateAt == null) {
            throw new IllegalArgumentException("Certificate date must not be null");
        }
    }
}

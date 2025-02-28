package sleppynavigators.studyupbackend.domain.challenge.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import sleppynavigators.studyupbackend.infrastructure.common.attribute.converter.StringConverter;

@Embeddable
public record TaskCertification(@Convert(converter = StringConverter.class) @Column List<URL> externalLinks,
                                @Convert(converter = StringConverter.class) @Column List<URL> imageUrls,
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

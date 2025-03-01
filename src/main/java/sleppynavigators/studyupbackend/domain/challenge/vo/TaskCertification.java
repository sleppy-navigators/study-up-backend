package sleppynavigators.studyupbackend.domain.challenge.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import sleppynavigators.studyupbackend.infrastructure.common.attribute.converter.UrlConverter;

@Embeddable
public record TaskCertification(
        @Convert(converter = UrlConverter.class) @Column(nullable = false) List<URL> externalLinks,
        @Convert(converter = UrlConverter.class) @Column(nullable = false) List<URL> imageUrls,
        @Column(nullable = false) LocalDateTime certificateAt) {

    public TaskCertification {
        if (externalLinks.isEmpty() && imageUrls.isEmpty()) {
            throw new IllegalArgumentException("At least one external link or image URL must be provided");
        }
    }
}

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
        @Column(nullable = false) @Convert(converter = UrlConverter.class) List<URL> externalLinks,
        @Column(nullable = false) @Convert(converter = UrlConverter.class) List<URL> imageUrls,
        @Column LocalDateTime certifiedAt) {

    public TaskCertification {
        if (isCertified() && noCertificationProvided()) {
            throw new IllegalArgumentException("At least one external link or image URL must be provided");
        }
    }

    public boolean isCertified() {
        return certifiedAt != null;
    }

    private boolean noCertificationProvided() {
        assert externalLinks != null && imageUrls != null;
        return externalLinks.isEmpty() && imageUrls.isEmpty();
    }
}

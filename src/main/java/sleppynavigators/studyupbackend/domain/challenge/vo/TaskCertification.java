package sleppynavigators.studyupbackend.domain.challenge.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sleppynavigators.studyupbackend.infrastructure.common.jpa.attribute.converter.UrlConverter;

@Embeddable
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
public class TaskCertification {

    @Column(nullable = false)
    @Convert(converter = UrlConverter.class)
    private List<URL> externalLinks;

    @Column(nullable = false)
    @Convert(converter = UrlConverter.class)
    private List<URL> imageUrls;

    @Column
    private LocalDateTime certifiedAt;

    public TaskCertification(List<URL> externalLinks, List<URL> imageUrls, LocalDateTime certifiedAt) {
        this.externalLinks = externalLinks;
        this.imageUrls = imageUrls;
        this.certifiedAt = certifiedAt;

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

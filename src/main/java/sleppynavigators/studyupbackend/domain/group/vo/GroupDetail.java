package sleppynavigators.studyupbackend.domain.group.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record GroupDetail(@Column(nullable = false) String name,
                          @Column(nullable = false) String description,
                          @Column String thumbnailUrl) {
}

package sleppynavigators.studyupbackend.domain.group.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record GroupInfo(@Column(nullable = false) String name,
                        @Column(nullable = false) String description,
                        String thumbnailUrl) {
}

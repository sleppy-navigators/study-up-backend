package sleppynavigators.studyupbackend.domain.user.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record UserProfile(@Column(nullable = false) String username,
                          @Column(nullable = false) String email) {
}

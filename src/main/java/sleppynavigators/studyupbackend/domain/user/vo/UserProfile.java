package sleppynavigators.studyupbackend.domain.user.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
public class UserProfile {

    @Column(nullable = false)
    String username;

    @Column(nullable = false)
    String email;

    public UserProfile(String username, String email) {
        this.username = username;
        this.email = email;
    }
}

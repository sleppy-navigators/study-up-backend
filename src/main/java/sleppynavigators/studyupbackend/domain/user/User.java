package sleppynavigators.studyupbackend.domain.user;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;

@Entity(name = "users")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private UserProfile userProfile;

    public User(String username, String email) {
        this.userProfile = new UserProfile(username, email);
    }
}

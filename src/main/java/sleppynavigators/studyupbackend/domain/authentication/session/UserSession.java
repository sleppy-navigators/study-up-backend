package sleppynavigators.studyupbackend.domain.authentication.session;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sleppynavigators.studyupbackend.domain.user.User;

@Entity(name = "user_sessions")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String refreshToken;

    @Column(length = 512)
    private String accessToken;

    @Column
    private LocalDateTime expiration;

    public UserSession(User user, String refreshToken, String accessToken, LocalDateTime expiration) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.expiration = expiration;
    }

    public boolean isAlive() {
        return expiration != null && LocalDateTime.now().isBefore(expiration);
    }

    public boolean isRegistered(String refreshToken, String accessToken) {
        if (this.refreshToken == null || this.accessToken == null) {
            return false;
        }

        return this.refreshToken.equals(refreshToken) && this.accessToken.equals(accessToken);
    }

    public void update(String refreshToken, String accessToken, LocalDateTime expiration) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.expiration = expiration;
    }
}

package sleppynavigators.studyupbackend.domain.authentication.session;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sleppynavigators.studyupbackend.domain.user.User;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class UserSession {

    @Id
    @Column
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn
    @NotNull
    private User user;

    @Column
    private String refreshToken;

    @Column
    @Size(max = 512)
    private String accessToken;

    @Column
    private LocalDateTime expiration;

    public UserSession(User user, String refreshToken, String accessToken, LocalDateTime expiration) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.expiration = expiration;
    }

    public boolean isExpired() {
        return expiration == null || LocalDateTime.now().isAfter(expiration);
    }

    public boolean isValidToken(String refreshToken, String accessToken) {
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

package sleppynavigators.studyupbackend.domain.user;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;
import sleppynavigators.studyupbackend.domain.common.TimeAuditBaseEntity;
import sleppynavigators.studyupbackend.domain.point.Point;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;

@SoftDelete
@Entity(name = "users")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class User extends TimeAuditBaseEntity {

    private static final Long INITIAL_EQUITY = 1_000L;

    @Embedded
    private UserProfile userProfile;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "point_id", nullable = false)
    private Point equity;

    public User(String username, String email) {
        this.userProfile = new UserProfile(username, email);
        this.equity = new Point(INITIAL_EQUITY);
    }
}

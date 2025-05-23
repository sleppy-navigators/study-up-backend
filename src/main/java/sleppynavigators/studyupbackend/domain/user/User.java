package sleppynavigators.studyupbackend.domain.user;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;
import sleppynavigators.studyupbackend.domain.common.TimeAuditBaseEntity;
import sleppynavigators.studyupbackend.domain.point.vo.Point;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;
import sleppynavigators.studyupbackend.exception.business.InSufficientPointsException;

@SoftDelete
@Entity(name = "users")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class User extends TimeAuditBaseEntity {

    private static final Long INITIAL_EQUITY = 1_000L;

    @Embedded
    private UserProfile userProfile;

    @Embedded
    private Point equity;

    public User(String username, String email) {
        this.userProfile = new UserProfile(username, email);
        this.equity = new Point(INITIAL_EQUITY);
    }

    public void grantEquity(Long amount) {
        equity = equity.add(amount);
    }

    public void deductEquity(Long amount) {
        if (equity.getAmount() < amount) {
            throw new InSufficientPointsException(
                    "Insufficient equity to deduct - current equity: " + amount);
        }

        equity = equity.subtract(amount);
    }
}

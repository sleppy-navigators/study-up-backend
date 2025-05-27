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

    private static final Long INITIAL_POINT = 1_000L;

    @Embedded
    private UserProfile userProfile;

    @Embedded
    private Point point;

    public User(String username, String email) {
        this.userProfile = new UserProfile(username, email);
        this.point = new Point(INITIAL_POINT);
    }

    public void grantPoint(Long amount) {
        point = point.add(amount);
    }

    public void deductPoint(Long amount) {
        if (point.getAmount() < amount) {
            throw new InSufficientPointsException(
                    "Insufficient equity to deduct - current equity: " + point.getAmount() + ", requested: " + amount);
        }

        point = point.subtract(amount);
    }
}

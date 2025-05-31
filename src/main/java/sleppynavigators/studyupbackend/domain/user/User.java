package sleppynavigators.studyupbackend.domain.user;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;
import sleppynavigators.studyupbackend.domain.common.TimeAuditBaseEntity;
import sleppynavigators.studyupbackend.domain.point.vo.Point;
import sleppynavigators.studyupbackend.domain.user.following.Following;
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

    @OneToMany(mappedBy = "followee", fetch = FetchType.LAZY)
    private List<Following> followers = new ArrayList<>();

    @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Following> followings = new ArrayList<>();

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

    public void startFollowing(User followee) {
        if (isCurrentlyFollowing(followee)) {
            return;
        }

        Following following = new Following(this, followee);
        followings.add(following);
        followee.getFollowers().add(following);
    }

    public void stopFollowing(User followee) {
        followings.stream()
                .filter(f -> f.getFollowee().equals(followee))
                .findFirst()
                .ifPresent(f -> {
                    followings.remove(f);
                    followee.getFollowers().remove(f);
                });
    }

    private boolean isCurrentlyFollowing(User user) {
        return followings.stream()
                .anyMatch(following -> following.getFollowee().equals(user));
    }
}

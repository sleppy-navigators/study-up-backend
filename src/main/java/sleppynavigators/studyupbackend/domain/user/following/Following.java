package sleppynavigators.studyupbackend.domain.user.following;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sleppynavigators.studyupbackend.domain.common.TimeAuditBaseEntity;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.business.InvalidPayloadException;

@Entity(name = "followings")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Following extends TimeAuditBaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "followee_id", nullable = false)
    private User followee;

    public Following(User follower, User followee) {
        if (follower.equals(followee)) {
            throw new InvalidPayloadException("Followers and followees are the same");
        }

        this.follower = follower;
        this.followee = followee;
    }
}

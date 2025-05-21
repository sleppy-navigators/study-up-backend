package sleppynavigators.studyupbackend.domain.point;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.common.UserAndTimeAuditBaseEntity;
import sleppynavigators.studyupbackend.domain.user.User;

@SoftDelete
@Entity(name = "points")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Point extends UserAndTimeAuditBaseEntity {

    @Column(nullable = false)
    private Long amount;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", updatable = false)
    private User user;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "challenge_id", updatable = false)
    private Challenge challenge;

    private Point(Long amount) {
        validateAmount(amount);
        this.amount = amount;
    }

    public Point(Long amount, User user) {
        this(amount);
        this.user = user;
    }

    public Point(Long amount, Challenge challenge) {
        this(amount);
        this.challenge = challenge;
    }

    public void add(Long amount) {
        validateAmount(this.amount + amount);
        this.amount += amount;
    }

    public void subtract(Long amount) {
        if (this.amount < amount) {
            throw new IllegalArgumentException("Insufficient points for subtraction");
        }

        this.amount -= amount;
    }

    public Boolean isSufficientFor(Long amount) {
        return this.amount >= amount;
    }

    private void validateAmount(Long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be a non-negative number");
        }
    }
}

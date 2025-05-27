package sleppynavigators.studyupbackend.domain.challenge.hunting.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sleppynavigators.studyupbackend.domain.point.vo.Point;

@Embeddable
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
public class Deposit {

    private static final double REWARD_RATE = 0.1;

    @Column(nullable = false, updatable = false)
    private Long initialAmount;

    @Embedded
    private Point remain;

    public Deposit(Long initialAmount) {
        this.initialAmount = initialAmount;
        this.remain = new Point(initialAmount);
    }

    public Point calculateReward() {
        return remain.multiply(1 + REWARD_RATE);
    }

    public void subtract(Long amount) {
        remain = remain.subtract(amount);
    }
}

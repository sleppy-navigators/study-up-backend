package sleppynavigators.studyupbackend.domain.challenge.hunting.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import sleppynavigators.studyupbackend.domain.point.vo.Point;

@Embeddable
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
public class Deposit {

    private static final double REWARD_RATE = 0.1;

    @Column(nullable = false, updatable = false)
    private Long initialAmount;

    @With
    @Embedded
    private Point remain;

    public Deposit(Long initialAmount) {
        this(initialAmount, new Point(initialAmount));
    }

    private Deposit(Long initialAmount, Point remain) {
        this.initialAmount = initialAmount;
        this.remain = remain;
    }

    public Point calculateReward() {
        return remain.multiply(1 + REWARD_RATE);
    }

    public Deposit subtract(Long amount) {
        return withRemain(remain.subtract(amount));
    }
}

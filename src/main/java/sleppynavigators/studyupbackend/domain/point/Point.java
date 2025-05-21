package sleppynavigators.studyupbackend.domain.point;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;
import sleppynavigators.studyupbackend.domain.common.UserAndTimeAuditBaseEntity;

@SoftDelete
@Entity(name = "points")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Point extends UserAndTimeAuditBaseEntity {

    @Column(nullable = false)
    Long amount;

    public Point(Long amount) {
        validateAmount(amount);
        this.amount = amount;
    }

    public void addWithAdditionalRate(Point other, Double rate) {
        if (rate < 0) {
            throw new IllegalArgumentException("Rate must be a non-negative number");
        }

        Long additionalAmount = Math.round(other.amount * rate);
        this.amount += other.amount + additionalAmount;
    }

    public void add(Point other) {
        this.amount += other.amount;
    }

    public void subtract(Point other) {
        if (this.amount < other.amount) {
            throw new IllegalArgumentException("Insufficient points for subtraction");
        }

        this.amount -= other.amount;
    }

    public Boolean isSufficientFor(Point other) {
        return this.amount >= other.amount;
    }

    private void validateAmount(Long amount) {
        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("Amount must be a non-negative number");
        }
    }
}

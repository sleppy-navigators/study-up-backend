package sleppynavigators.studyupbackend.domain.point.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
public class Point {

    @Column(nullable = false)
    private Long amount;

    public Point(Long amount) {
        validateAmount(amount);

        this.amount = amount;
    }

    public Point add(Long amount) {
        return new Point(this.amount + amount);
    }

    public Point subtract(Long amount) {
        return new Point(this.amount - amount);
    }

    public Point multiply(Double rate) {
        return new Point(Math.round(this.amount * rate));
    }

    private void validateAmount(Long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be a non-negative number");
        }
    }
}

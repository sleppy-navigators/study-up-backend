package sleppynavigators.studyupbackend.domain.challenge.hunting;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.common.TimeAuditBaseEntity;
import sleppynavigators.studyupbackend.domain.point.vo.Point;
import sleppynavigators.studyupbackend.domain.user.User;

@SoftDelete
@Entity(name = "huntings")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Hunting extends TimeAuditBaseEntity {

    @Embedded
    private Point reward;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_id", nullable = false, updatable = false)
    private Task target;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hunter_id", nullable = false, updatable = false)
    private User hunter;

    public Hunting(Long reward, Task target, User hunter) {
        this.reward = new Point(reward);
        this.target = target;
        this.hunter = hunter;
    }

    public boolean isHunter(User user) {
        return this.hunter.equals(user);
    }
}

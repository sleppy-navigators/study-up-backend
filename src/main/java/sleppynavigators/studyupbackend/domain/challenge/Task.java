package sleppynavigators.studyupbackend.domain.challenge;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.SoftDelete;
import sleppynavigators.studyupbackend.domain.challenge.vo.TaskCertification;
import sleppynavigators.studyupbackend.domain.challenge.vo.TaskDetail;
import sleppynavigators.studyupbackend.domain.common.TimeAuditBaseEntity;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.business.ForbiddenContentException;
import sleppynavigators.studyupbackend.exception.business.OveredDeadlineException;

@SoftDelete
@Entity(name = "tasks")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Task extends TimeAuditBaseEntity {

    @Immutable
    @Embedded
    private TaskDetail detail;

    @Embedded
    private TaskCertification certification;

    @Immutable
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    public Task(String title, LocalDateTime deadline, Challenge challenge) {
        this.detail = new TaskDetail(title, deadline);
        this.challenge = challenge;
        this.certification = new TaskCertification(new ArrayList<>(), new ArrayList<>(), null);
    }

    public void certify(List<URL> externalLinks, List<URL> imageUrls, User certifier) {
        if (!challenge.canModify(certifier)) {
            throw new ForbiddenContentException();
        }

        if (detail.isOverdue()) {
            throw new OveredDeadlineException();
        }

        this.certification = new TaskCertification(externalLinks, imageUrls, LocalDateTime.now());
    }

    public boolean isCompleted() {
        return isSucceed() || isFailed();
    }

    public boolean isSucceed() {
        return certification.isCertified();
    }

    public boolean isFailed() {
        return detail.isOverdue() && !certification.isCertified();
    }
}

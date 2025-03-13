package sleppynavigators.studyupbackend.domain.authentication;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.SoftDelete;
import sleppynavigators.studyupbackend.domain.common.TimeAuditBaseEntity;
import sleppynavigators.studyupbackend.domain.user.User;

@SoftDelete
@Entity(name = "user_credentials")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class UserCredential extends TimeAuditBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subject;

    // should consider changing the type from `String` to `Enum`.
    @Column(nullable = false)
    private String provider;

    @Immutable
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public UserCredential(String subject, String provider, User user) {
        this.subject = subject;
        this.provider = provider;
        this.user = user;
    }
}

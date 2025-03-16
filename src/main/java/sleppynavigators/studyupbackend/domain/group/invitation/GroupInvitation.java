package sleppynavigators.studyupbackend.domain.group.invitation;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sleppynavigators.studyupbackend.domain.common.TimeAuditBaseEntity;
import sleppynavigators.studyupbackend.domain.common.UserAuditAttribute;
import sleppynavigators.studyupbackend.infrastructure.common.attribute.listener.UserAuditListener;
import sleppynavigators.studyupbackend.domain.group.Group;

@Entity(name = "group_invitations")
@Getter
@EntityListeners(UserAuditListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupInvitation extends TimeAuditBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private UserAuditAttribute userAuditAttribute;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false, updatable = false)
    private Group group;

    @Column(nullable = false)
    private String invitationKey;

    // We might add expiration date to the field later, but we won't add it now.

    public GroupInvitation(Group group) {
        this.group = group;
        this.invitationKey = UUID.randomUUID().toString();
    }

    public boolean matchGroupId(Long groupId) {
        return this.group.getId().equals(groupId);
    }

    public boolean matchKey(String key) {
        return this.invitationKey.equals(key);
    }
}

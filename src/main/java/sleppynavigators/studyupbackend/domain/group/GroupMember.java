package sleppynavigators.studyupbackend.domain.group;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sleppynavigators.studyupbackend.domain.user.User;

@Entity(name = "group_members")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // There is room to add fields for group permissions in the future.
    // For now, we only need to know the group and the user.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public GroupMember(Group group, User user) {
        this.group = group;
        this.user = user;
    }
}

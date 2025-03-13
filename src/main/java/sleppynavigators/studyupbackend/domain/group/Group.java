package sleppynavigators.studyupbackend.domain.group;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sleppynavigators.studyupbackend.domain.common.TimeAuditBaseEntity;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.group.vo.GroupDetail;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.business.ActionRequiredBeforeException;

@Entity(name = "`groups`")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Group extends TimeAuditBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private GroupDetail groupDetail;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> members;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private List<Challenge> challenges;

    // We won't add system messages to the field
    // because we'll manage them in a separate database.
    // The target of the indirect reference will be the system message, not the group.

    @Builder
    public Group(String name, String description, String thumbnailUrl, User creator) {
        this.groupDetail = new GroupDetail(name, description, thumbnailUrl);
        this.members = new ArrayList<>();
        this.challenges = new ArrayList<>();

        addMember(creator);
    }

    public void addMember(User member) {
        members.add(new GroupMember(this, member));
    }

    public void removeMember(GroupMember member) {
        if (isChallengeOwner(member.getUser())) {
            throw new ActionRequiredBeforeException("Challenger cannot leave the group.");
        }

        members.remove(member);
    }

    public boolean hasAnyMember() {
        return !members.isEmpty();
    }

    public boolean hasMember(User user) {
        return members.stream()
                .anyMatch(member -> member.getUser().equals(user));
    }

    private boolean isChallengeOwner(User user) {
        return challenges.stream()
                .anyMatch(challenge -> challenge.getOwner().equals(user));
    }
}

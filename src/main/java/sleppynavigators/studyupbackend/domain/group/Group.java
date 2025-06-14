package sleppynavigators.studyupbackend.domain.group;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.common.UserAndTimeAuditBaseEntity;
import sleppynavigators.studyupbackend.domain.group.vo.GroupDetail;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.business.ActionRequiredBeforeException;

@SoftDelete
@Entity(name = "`groups`")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Group extends UserAndTimeAuditBaseEntity {

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
    public Group(String name, String description, URL thumbnailUrl, User creator) {
        this.groupDetail = new GroupDetail(name, description, thumbnailUrl);
        this.members = new ArrayList<>();
        this.challenges = new ArrayList<>();

        addMember(creator);
    }

    public void addMember(User member) {
        if (hasMember(member)) {
            return;
        }

        members.add(new GroupMember(this, member));
    }

    public void removeMember(GroupMember member) {
        if (isChallenger(member.getUser())) {
            throw new ActionRequiredBeforeException(
                    "Challenger cannot leave the group - userId: " + member.getUser().getId() +
                            ", groupId: " + getId());
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

    public int getNumOfMembers() {
        return members.size();
    }

    private boolean isChallenger(User user) {
        return challenges.stream()
                .filter(challenge -> !challenge.isCompleted())
                .anyMatch(challenge -> challenge.isOwner(user));
    }
}

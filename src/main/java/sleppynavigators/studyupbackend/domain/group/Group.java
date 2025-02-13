package sleppynavigators.studyupbackend.domain.group;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sleppynavigators.studyupbackend.domain.group.vo.GroupInfo;
import sleppynavigators.studyupbackend.domain.user.User;

@Entity(name = "user_groups")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private GroupInfo groupInfo;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> members;

    // We'll add challenges to the field later, but we won't add system messages to the field
    // because we'll manage them in a separate database.
    // The target of the indirect reference will be the system message, not the group.

    @Builder
    public Group(String name, String description, String thumbnailUrl, User creator) {
        this.groupInfo = new GroupInfo(name, description, thumbnailUrl);
        this.members = new ArrayList<>();

        if (creator != null) {
            addMember(creator);
        }
    }

    public void addMember(User member) {
        members.add(new GroupMember(this, member));
    }

    public void removeMember(GroupMember member) {
        members.remove(member);
    }

    public boolean hasAnyMember() {
        return !members.isEmpty();
    }
}

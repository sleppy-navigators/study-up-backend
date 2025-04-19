package sleppynavigators.studyupbackend.domain.chat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sleppynavigators.studyupbackend.domain.common.TimeAuditBaseEntity;
import sleppynavigators.studyupbackend.domain.group.Group;

@Entity(name = "bots")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bot extends TimeAuditBaseEntity {

    private static final String BOT_NAME = "StudyUpBot";

    @Column(nullable = false)
    private String name = BOT_NAME;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    public Bot(Group group) {
        this.group = group;
    }
}

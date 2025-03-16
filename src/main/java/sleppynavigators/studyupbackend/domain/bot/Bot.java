package sleppynavigators.studyupbackend.domain.bot;

import jakarta.persistence.*;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name = BOT_NAME;
    
    @OneToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    public Bot(Group group) {
        this.group = group;
    }
}

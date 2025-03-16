package sleppynavigators.studyupbackend.domain.bot;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sleppynavigators.studyupbackend.domain.common.BaseTimeEntity;
import sleppynavigators.studyupbackend.domain.group.Group;

@Entity
@Table(name = "bots")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bot extends BaseTimeEntity {

    private static final String BOT_NAME = "StudyUpBot";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Builder.Default
    @Column(nullable = false)
    private String name = BOT_NAME;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;
    
    @Builder
    protected Bot(String name, Group group) {
        this.name = name;
        this.group = group;
    }
} 

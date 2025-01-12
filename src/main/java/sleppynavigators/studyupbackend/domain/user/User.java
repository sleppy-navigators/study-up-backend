package sleppynavigators.studyupbackend.domain.user;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sleppynavigators.studyupbackend.domain.user.vo.SampleVO;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private SampleVO sample;

    public User(String sampleMessage) {
        this.sample = new SampleVO(sampleMessage);
    }
}

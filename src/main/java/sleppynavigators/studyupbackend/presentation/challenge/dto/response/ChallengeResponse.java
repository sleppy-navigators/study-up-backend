package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import java.time.LocalDateTime;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;

public record ChallengeResponse(Long id, String title, LocalDateTime deadline, String description) {

    public static ChallengeResponse fromEntity(Challenge challenge) {
        return new ChallengeResponse(
                challenge.getId(),
                challenge.getDetail().title(),
                challenge.getDetail().deadline(),
                challenge.getDetail().description()
        );
    }
}

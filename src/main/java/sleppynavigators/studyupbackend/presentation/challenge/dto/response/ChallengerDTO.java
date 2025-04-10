package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import jakarta.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;

public record ChallengerDTO(@NotNull Long challengerId,
                            @NotBlank String challengerName,
                            @NotNull Boolean currentlyJoined) {

    public static ChallengerDTO fromEntity(Challenge challenge) {
        return new ChallengerDTO(
                challenge.getOwner().getId(),
                challenge.getOwner().getUserProfile().username(),
                challenge.getGroup().hasMember(challenge.getOwner()));
    }
}

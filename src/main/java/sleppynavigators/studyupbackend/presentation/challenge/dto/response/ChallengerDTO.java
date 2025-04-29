package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;

@Schema(description = "챌린저")
public record ChallengerDTO(
        @Schema(description = "챌린저 ID", example = "1")
        @NotNull Long challengerId,

        @Schema(description = "챌린저 이름", example = "홍길동")
        @NotBlank String challengerName,

        @Schema(description = "현재 그룹 가입 여부", example = "true")
        @NotNull Boolean currentlyJoined) {

    public static ChallengerDTO fromEntity(Challenge challenge) {
        return new ChallengerDTO(
                challenge.getOwner().getId(),
                challenge.getOwner().getUserProfile().getUsername(),
                challenge.getGroup().hasMember(challenge.getOwner()));
    }
}

package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import sleppynavigators.studyupbackend.domain.challenge.hunting.Hunting;

@Schema(description = "헌팅 결과 응답")
public record HuntingResponse(@Schema(description = "헌팅 보상 포인트", example = "1000")
                              @NotNull Long point) {

    public static HuntingResponse fromEntity(Hunting hunting) {
        return new HuntingResponse(hunting.getReward().getAmount());
    }
}

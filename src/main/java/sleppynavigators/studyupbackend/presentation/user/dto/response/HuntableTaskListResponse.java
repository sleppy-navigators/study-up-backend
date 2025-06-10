package sleppynavigators.studyupbackend.presentation.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.ChallengerDTO;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskChallengeDTO;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskGroupDTO;

@Schema(description = "헌팅 가능한 테스크 목록 응답")
public record HuntableTaskListResponse(@NotNull @Valid List<HuntableTaskListItem> tasks) {

    public static HuntableTaskListResponse fromEntities(List<Task> tasks) {
        return new HuntableTaskListResponse(
                tasks.stream()
                        .map(HuntableTaskListItem::fromEntity)
                        .toList());
    }

    @Schema(description = "헌팅 가능한 테스크")
    public record HuntableTaskListItem(
            @Schema(description = "테스크 ID", example = "1")
            @NotNull Long id,

            @Schema(description = "테스크 제목", example = "과제 제출하기")
            @NotBlank String title,

            @Schema(description = "헌팅 보상", example = "1000")
            @NotNull Long reward,

            @Schema(description = "챌린저 정보")
            @NotNull @Valid ChallengerDTO challengerDetail,

            @Schema(description = "테스크 챌린지 정보")
            @NotNull @Valid TaskChallengeDTO challengeDetail,

            @Schema(description = "테스크 그룹 정보")
            @NotNull @Valid TaskGroupDTO groupDetail) {

        public static HuntableTaskListItem fromEntity(Task task) {
            return new HuntableTaskListItem(
                    task.getId(),
                    task.getDetail().getTitle(),
                    task.calcHuntingReward(),
                    ChallengerDTO.fromEntity(task.getChallenge()),
                    TaskChallengeDTO.fromEntity(task),
                    TaskGroupDTO.fromEntity(task));
        }
    }
}

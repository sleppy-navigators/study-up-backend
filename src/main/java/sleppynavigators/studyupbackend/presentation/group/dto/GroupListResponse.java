package sleppynavigators.studyupbackend.presentation.group.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record GroupListResponse(@NotNull List<GroupListItem> groups) {

    public record GroupListItem(@NotNull Long id,
                                @NotNull String name,
                                @Email String thumbnailUrl,
                                @NotBlank String lastSystemMessage) {
    }
}

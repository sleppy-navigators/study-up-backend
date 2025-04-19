package sleppynavigators.studyupbackend.presentation.group.dto.response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record GroupListResponse(@NotNull @Valid List<GroupDTO> groups) {
}

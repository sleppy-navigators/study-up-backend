package sleppynavigators.studyupbackend.presentation.user.dto;

import sleppynavigators.studyupbackend.domain.user.User;

public record SampleResponse(String message) {
    public static SampleResponse from(User user) {
        return new SampleResponse(user.getSample().message());
    }
}

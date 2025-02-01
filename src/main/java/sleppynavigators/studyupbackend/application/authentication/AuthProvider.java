package sleppynavigators.studyupbackend.application.authentication;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthProvider {

    GOOGLE("google");

    private final String provider;
}

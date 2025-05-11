package sleppynavigators.studyupbackend.application.authentication;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public enum AuthProvider {

    GOOGLE("google");

    private final String provider;
}

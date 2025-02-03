package sleppynavigators.studyupbackend.presentation.authentication.filter;

import java.security.Principal;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;

public record UserPrincipal(Long userId, UserProfile userProfile) implements Principal {

    @Override
    public String getName() {
        return userProfile.username();
    }
}

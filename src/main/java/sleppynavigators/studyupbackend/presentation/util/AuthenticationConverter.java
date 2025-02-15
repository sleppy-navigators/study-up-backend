package sleppynavigators.studyupbackend.presentation.util;

import java.util.List;
import org.springframework.security.core.Authentication;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessToken;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;
import sleppynavigators.studyupbackend.presentation.authentication.filter.UserAuthentication;
import sleppynavigators.studyupbackend.presentation.authentication.filter.UserPrincipal;

public class AuthenticationConverter {

    public static Authentication convertToAuthentication(AccessToken accessToken) {
        Long userId = accessToken.getUserId();
        UserProfile userProfile = accessToken.getUserProfile();
        UserPrincipal userPrincipal = new UserPrincipal(userId, userProfile);
        List<String> authorities = accessToken.getAuthorities();
        return new UserAuthentication(userPrincipal, authorities);
    }
}

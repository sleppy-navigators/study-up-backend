package sleppynavigators.studyupbackend.presentation.common;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;
import sleppynavigators.studyupbackend.presentation.authentication.filter.UserAuthentication;
import sleppynavigators.studyupbackend.presentation.authentication.filter.UserPrincipal;

public class MockUserAuthenticationFactory implements WithSecurityContextFactory<WithMockedUserInfo> {

    @Override
    public SecurityContext createSecurityContext(WithMockedUserInfo mockedUserInfo) {
        UserPrincipal principal = new UserPrincipal(mockedUserInfo.userId(),
                new UserProfile(mockedUserInfo.username(), mockedUserInfo.email()));
        Authentication authentication = new UserAuthentication(principal, List.of("profile"));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}

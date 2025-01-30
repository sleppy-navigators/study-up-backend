package sleppynavigators.studyupbackend.presentation.authentication.filter;

import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;

@RequiredArgsConstructor
public class UserAuthentication implements Authentication {

    private final Long userId;
    private final UserProfile userProfile;
    private final List<String> authorities;
    private boolean authenticated = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return userProfile;
    }

    public Object getPrincipal() {
        return userId;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return userProfile.username();
    }
}

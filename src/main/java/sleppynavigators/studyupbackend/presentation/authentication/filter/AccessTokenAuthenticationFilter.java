package sleppynavigators.studyupbackend.presentation.authentication.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessToken;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessTokenProperties;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;
import sleppynavigators.studyupbackend.exception.ErrorResponse;
import sleppynavigators.studyupbackend.exception.BaseException;
import sleppynavigators.studyupbackend.exception.business.SessionExpiredException;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AccessTokenAuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final AccessTokenProperties accessTokenProperties;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String bearerToken = getBearerToken(request);
            AccessToken accessToken = AccessToken.deserialize(bearerToken, accessTokenProperties);

            if (accessToken.isExpired()) {
                response.setContentType("application/json");
                BaseException exception = new SessionExpiredException();
                response.setStatus(exception.getStatus());
                objectMapper.writeValue(response.getWriter(),
                        new ErrorResponse(exception.getCode(), exception.getMessage(), request.getRequestURI()));
                return;
            }

            Authentication authentication = converToAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException ignored) {
        }

        filterChain.doFilter(request, response);
    }

    private String getBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }

        return authorization.substring(7 /* = "Bearer ".length */);
    }

    private Authentication converToAuthentication(AccessToken accessToken) {
        Long userId = accessToken.getUserId();
        UserProfile userProfile = accessToken.getUserProfile();
        UserPrincipal userPrincipal = new UserPrincipal(userId, userProfile);
        List<String> authorities = accessToken.getAuthorities();
        return new UserAuthentication(userPrincipal, authorities);
    }
}

package sleppynavigators.studyupbackend.presentation.authentication.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessToken;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessTokenProperties;
import sleppynavigators.studyupbackend.exception.BaseException;
import sleppynavigators.studyupbackend.exception.ErrorResponse;
import sleppynavigators.studyupbackend.exception.business.SessionExpiredException;
import sleppynavigators.studyupbackend.presentation.util.AuthenticationConverter;
import sleppynavigators.studyupbackend.presentation.util.BearerTokenExtractor;

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
            String bearerToken = BearerTokenExtractor.extractFromRequest(request);
            AccessToken accessToken = AccessToken.deserialize(bearerToken, accessTokenProperties);

            if (accessToken.isExpired()) {
                response.setContentType("application/json");
                BaseException exception = new SessionExpiredException();
                response.setStatus(exception.getStatus());
                objectMapper.writeValue(response.getWriter(),
                        new ErrorResponse(exception.getCode(), exception.getMessage(), request.getRequestURI()));
                return;
            }

            Authentication authentication = AuthenticationConverter.convertToAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException ignored) {
        }

        filterChain.doFilter(request, response);
    }
}

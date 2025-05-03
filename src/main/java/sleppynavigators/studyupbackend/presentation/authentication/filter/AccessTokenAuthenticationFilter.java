package sleppynavigators.studyupbackend.presentation.authentication.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessToken;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessTokenProperties;
import sleppynavigators.studyupbackend.exception.BaseException;
import sleppynavigators.studyupbackend.exception.ErrorResponse;
import sleppynavigators.studyupbackend.exception.business.SessionExpiredException;
import sleppynavigators.studyupbackend.exception.network.InvalidCredentialException;
import sleppynavigators.studyupbackend.presentation.common.util.AuthenticationConverter;
import sleppynavigators.studyupbackend.presentation.common.util.BearerTokenExtractor;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AccessTokenAuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final AccessTokenProperties accessTokenProperties;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String bearerToken = BearerTokenExtractor.extractFromRequest(request);
            AccessToken accessToken = AccessToken.deserialize(bearerToken, accessTokenProperties);

            if (accessToken.isExpired()) {
                response.setContentType("application/json");
                BaseException exception = new SessionExpiredException("Access token is expired");
                response.setStatus(exception.getStatus());
                objectMapper.writeValue(response.getWriter(),
                        new ErrorResponse(exception.getCode(), exception.getMessage(), request.getRequestURI()));
                return;
            }

            Authentication authentication = AuthenticationConverter.convertToAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (InvalidCredentialException ignored) {
        } catch (RuntimeException ex) {
            log.error(ex.getMessage(), ex);
        }

        filterChain.doFilter(request, response);
    }
}

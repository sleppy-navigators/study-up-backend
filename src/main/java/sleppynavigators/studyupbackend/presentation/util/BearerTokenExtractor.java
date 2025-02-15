package sleppynavigators.studyupbackend.presentation.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

public class BearerTokenExtractor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    public static String extractFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER))
                .map(BearerTokenExtractor::extractFromHeader)
                .orElse(null);
    }

    public static String extractFromStompHeaders(StompHeaderAccessor accessor) {
        return Optional.ofNullable(accessor.getNativeHeader(AUTHORIZATION_HEADER))
                .filter(headers -> !headers.isEmpty())
                .map(headers -> headers.get(0))
                .map(BearerTokenExtractor::extractFromHeader)
                .orElse(null);
    }

    private static String extractFromHeader(String authorization) {
        return Optional.ofNullable(authorization)
                .filter(header -> header.startsWith(BEARER_PREFIX))
                .map(header -> header.substring(BEARER_PREFIX_LENGTH))
                .orElse(null);
    }
}

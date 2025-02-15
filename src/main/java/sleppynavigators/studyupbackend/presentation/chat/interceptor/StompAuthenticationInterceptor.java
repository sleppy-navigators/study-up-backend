package sleppynavigators.studyupbackend.presentation.chat.interceptor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessToken;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessTokenProperties;
import sleppynavigators.studyupbackend.exception.business.SessionExpiredException;
import sleppynavigators.studyupbackend.exception.network.InvalidCredentialException;
import sleppynavigators.studyupbackend.exception.network.UnAuthorizedException;
import sleppynavigators.studyupbackend.presentation.common.util.AuthenticationConverter;
import sleppynavigators.studyupbackend.presentation.common.util.BearerTokenExtractor;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class StompAuthenticationInterceptor implements ChannelInterceptor {

    private final AccessTokenProperties accessTokenProperties;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            try {
                String bearerToken = BearerTokenExtractor.extractFromStompHeaders(accessor);
                if (bearerToken == null) {
                    throw new InvalidCredentialException();
                }

                AccessToken accessToken = AccessToken.deserialize(bearerToken, accessTokenProperties);
                if (accessToken.isExpired()) {
                    throw new SessionExpiredException();
                }

                Authentication authentication = AuthenticationConverter.convertToAuthentication(accessToken);
                accessor.setUser(authentication);
            } catch (RuntimeException e) {
                throw new UnAuthorizedException("Unauthorized: " + e.getMessage());
            }
        }
        return message;
    }
}

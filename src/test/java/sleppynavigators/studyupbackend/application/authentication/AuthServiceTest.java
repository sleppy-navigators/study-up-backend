package sleppynavigators.studyupbackend.application.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sleppynavigators.studyupbackend.common.ApplicationBaseTest;
import sleppynavigators.studyupbackend.common.support.AuthSupport;
import sleppynavigators.studyupbackend.domain.authentication.UserCredential;
import sleppynavigators.studyupbackend.exception.network.InvalidCredentialException;
import sleppynavigators.studyupbackend.infrastructure.authentication.UserCredentialRepository;
import sleppynavigators.studyupbackend.infrastructure.authentication.oidc.GoogleOidcClient;
import sleppynavigators.studyupbackend.presentation.authentication.dto.request.SignInRequest;
import sleppynavigators.studyupbackend.presentation.authentication.dto.response.TokenResponse;

@DisplayName("[애플리케이션] AuthService 테스트")
class AuthServiceTest extends ApplicationBaseTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Autowired
    private AuthSupport authSupport;

    @MockitoBean
    private GoogleOidcClient googleOidcClient;

    @Test
    @DisplayName("기존 회원 구글 로그인 - 성공")
    void googleSignIn_ExistingMember_Success() {
        // given
        String idToken = "test-id-token";
        SignInRequest request = new SignInRequest(idToken);

        Claims idTokenClaims = Jwts.claims()
                .subject("test-subject")
                .add("name", "test-user")
                .add("email", "test-email")
                .build();
        given(googleOidcClient.deserialize(idToken)).willReturn(idTokenClaims);

        UserCredential userCredential = authSupport.registerUserCredentialToDB();

        // when
        TokenResponse response = authService.googleSignIn(request);

        // then
        then(googleOidcClient).should().deserialize(idToken);
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("신규 회원 구글 로그인 - 성공")
    void googleSignIn_NewMember_Success() {
        // given
        String idToken = "test-id-token";
        SignInRequest request = new SignInRequest(idToken);

        Claims idTokenClaims = Jwts.claims()
                .subject("test-subject")
                .add("name", "test-user")
                .add("email", "test-email")
                .build();
        given(googleOidcClient.deserialize(idToken)).willReturn(idTokenClaims);

        assert userCredentialRepository.findBySubject("test-subject").isEmpty();

        // when
        TokenResponse response = authService.googleSignIn(request);

        // then
        then(googleOidcClient).should().deserialize(idToken);
        assertThat(response).isNotNull();
        assertThat(userCredentialRepository.findBySubject("test-subject")).isNotEmpty();
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 구글 로그인 - 실패")
    void googleSignIn_InvalidToken_Fail() {
        // given
        String idToken = "invalid-id-token";
        SignInRequest request = new SignInRequest(idToken);
        given(googleOidcClient.deserialize(idToken)).willThrow(new InvalidCredentialException());

        // when & then
        assertThatThrownBy(() -> authService.googleSignIn(request))
                .isInstanceOf(InvalidCredentialException.class);
    }

    @TestConfiguration
    static class TestConfig {

        @Primary
        @Bean
        public GoogleOidcClient googleOidcClient() {
            return Mockito.mock(GoogleOidcClient.class);
        }
    }
}

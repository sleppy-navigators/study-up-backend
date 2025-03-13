package sleppynavigators.studyupbackend.application.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sleppynavigators.studyupbackend.domain.authentication.UserCredential;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.network.InvalidCredentialException;
import sleppynavigators.studyupbackend.infrastructure.authentication.UserCredentialRepository;
import sleppynavigators.studyupbackend.infrastructure.authentication.oidc.GoogleOidcClient;
import sleppynavigators.studyupbackend.presentation.authentication.dto.request.SignInRequest;
import sleppynavigators.studyupbackend.presentation.authentication.dto.response.TokenResponse;
import sleppynavigators.studyupbackend.presentation.common.DatabaseCleaner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("AuthService 테스트")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @MockitoBean
    private GoogleOidcClient googleOidcClient;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @TestConfiguration
    static class TestConfig {

        @Primary
        @Bean
        public GoogleOidcClient googleOidcClient() {
            return Mockito.mock(GoogleOidcClient.class);
        }
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.execute();
    }

    @Test
    @DisplayName("기존 회원의 구글 로그인을 성공적으로 수행한다")
    void memberGoogleSignIn_Success() {
        // given
        String idToken = "test-id-token";
        SignInRequest request = new SignInRequest(idToken);
        Claims idTokenClaims = Jwts.claims()
                .subject("test-subject")
                .add("name", "test-user")
                .add("email", "test-email")
                .build();
        given(googleOidcClient.deserialize(idToken)).willReturn(idTokenClaims);

        User user = new User("test-user", "test-email");
        UserCredential userCredential = new UserCredential("test-subject", "google", user);

        userCredentialRepository.save(userCredential);

        // when
        TokenResponse response = authService.googleSignIn(request);

        // then
        then(googleOidcClient).should().deserialize(idToken);
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("신규 회원의 구글 로그인을 성공적으로 수행한다")
    void newMemberGoogleSignIn_Success() {
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
    @DisplayName("유효하지 않은 id token으로 구글 로그인을 시도하면 예외가 발생한다")
    void whenInvalidIdToken_ThrowsInvalidCredentialException() {
        // given
        String idToken = "invalid-id-token";
        SignInRequest request = new SignInRequest(idToken);
        given(googleOidcClient.deserialize(idToken)).willThrow(new InvalidCredentialException());

        // when & then
        assertThatThrownBy(() -> authService.googleSignIn(request))
                .isInstanceOf(InvalidCredentialException.class);
    }
}

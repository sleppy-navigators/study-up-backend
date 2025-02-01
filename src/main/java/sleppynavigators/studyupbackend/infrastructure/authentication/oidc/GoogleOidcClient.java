package sleppynavigators.studyupbackend.infrastructure.authentication.oidc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.presentation.authentication.exception.InvalidCredentialException;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class GoogleOidcClient implements OidcClient {

    private static final String GOOGLE_CERT_URL = "https://www.googleapis.com/oauth2/v1/certs";
    private static final String GOOGLE_ISSUER = "https://accounts.google.com";
    private static final String GOOGLE_AUDIENCE = "425832297557-7serit12s908077334e928rclpdu6uqm.apps.googleusercontent.com";

    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;

    @Override
    public Claims deserialize(String idToken) {
        try {
            String kid = getKid(idToken);
            PublicKey publicKey = decodePublicKey(fetchPublicKey(kid));

            Claims claims = parseClaims(idToken, publicKey);
            if (!isTokenValid(claims)) {
                throw new InvalidCredentialException();
            }
            return claims;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JwtException e) {
            throw new InvalidCredentialException();
        }
    }

    private String getKid(String idToken) throws JsonProcessingException {
        String[] tokenParts = idToken.split("\\.");
        if (tokenParts.length != 3) {
            throw new IllegalArgumentException("Invalid id token");
        }

        String header = new String(Base64.getUrlDecoder().decode(tokenParts[0]));
        JsonNode headerJson = objectMapper.readTree(header);
        return headerJson.get("kid").asText();
    }

    private String fetchPublicKey(String kid) throws IOException {
        Request request = new Request.Builder()
                .url(GOOGLE_CERT_URL)
                .build();

        Call call = okHttpClient.newCall(request);
        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                log.error("Failed to get public key from Google: {}", response.body().string());
                throw new IOException("Failed to get public key from Google");
            }

            String responseBody = response.body().string();
            Map<?, ?> certs = objectMapper.readValue(responseBody, Map.class);
            return (String) certs.get(kid);
        }
    }

    private PublicKey decodePublicKey(String publicKey) {
        try {
            String trimmed = publicKey
                    .replace("-----BEGIN CERTIFICATE-----", "")
                    .replace("-----END CERTIFICATE-----", "")
                    .replaceAll("\\s", "");
            byte[] encoded = Base64.getDecoder().decode(trimmed);

            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory
                    .generateCertificate(new ByteArrayInputStream(encoded));
            return cert.getPublicKey();
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    private Claims parseClaims(String idToken, PublicKey publicKey) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(idToken)
                .getPayload();
    }

    private boolean isTokenValid(Claims claims) {
        return claims.getIssuer().equals(GOOGLE_ISSUER) && claims.getAudience().contains(GOOGLE_AUDIENCE);
    }
}

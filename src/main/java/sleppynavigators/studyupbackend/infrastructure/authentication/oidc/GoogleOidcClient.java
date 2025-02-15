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
import sleppynavigators.studyupbackend.exception.client.UnsuccessfulResponseException;
import sleppynavigators.studyupbackend.exception.network.InvalidCredentialException;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class GoogleOidcClient implements OidcClient {

    private static final String KEY_HEADER = "-----BEGIN CERTIFICATE-----";
    private static final String KEY_FOOTER = "-----END CERTIFICATE-----";
    private static final String CERTIFICATE_TYPE = "X.509";
    private static final CertificateFactory certFactory;

    static {
        try {
            certFactory = CertificateFactory.getInstance(CERTIFICATE_TYPE);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    private final GoogleProperties googleProperties;
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
        } catch (IOException | JwtException | IllegalArgumentException ignored) {
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

    private String fetchPublicKey(String kid) {
        Request request = new Request.Builder()
                .url(googleProperties.certificateUrl())
                .build();

        Call call = okHttpClient.newCall(request);
        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                log.warn("Failed to get public key from Google - {}", response.code());
                throw new IOException();
            }

            String responseBody = response.body().string();
            if (response.cacheResponse() != null) {
                log.info("Use cached Google public key");
            } else {
                log.info("Google public key response - {} {}", response.code(), responseBody);
            }

            Map<?, ?> certs = objectMapper.readValue(responseBody, Map.class);
            return (String) certs.get(kid);
        } catch (IOException e) {
            log.error("Failed to get public key from Google: {}", e.getMessage());
            throw new UnsuccessfulResponseException("Failed to get public key from Google");
        }
    }

    // TODO: extract to JwtUtils
    private PublicKey decodePublicKey(String publicKey) {
        try {
            String trimmed = publicKey
                    .replace(KEY_HEADER, "")
                    .replace(KEY_FOOTER, "")
                    .replaceAll("\\s", "");
            byte[] encoded = Base64.getDecoder().decode(trimmed);

            X509Certificate cert = (X509Certificate) certFactory
                    .generateCertificate(new ByteArrayInputStream(encoded));
            return cert.getPublicKey();
        } catch (CertificateException ignored) {
            throw new InvalidCredentialException();
        }
    }

    // TODO: extract to JwtUtils
    private Claims parseClaims(String idToken, PublicKey publicKey) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(idToken)
                .getPayload();
    }

    // TODO: extract to JwtUtils
    private boolean isTokenValid(Claims claims) {
        String issuer = googleProperties.issuer();
        String audience = googleProperties.audience();
        return claims.getIssuer().equals(issuer) && claims.getAudience().contains(audience);
    }
}

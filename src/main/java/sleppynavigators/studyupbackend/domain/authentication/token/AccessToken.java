package sleppynavigators.studyupbackend.domain.authentication.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;

@RequiredArgsConstructor
public class AccessToken {

    private final static String ISSUER = "study-up";

    private final Claims claims;

    public AccessToken(Long userId, UserProfile userProfile, List<String> authorities,
                       AccessTokenProperties properties) {
        String subject = String.valueOf(userId);
        String username = userProfile.username();
        String userEmail = userProfile.email();
        Long expirationInMilliseconds = properties.expirationInMilliseconds();

        Claims claims = makeClaims(subject, username, userEmail, authorities, expirationInMilliseconds);
        validateClaims(claims);
        this.claims = claims;
    }

    public static AccessToken deserialize(String token, AccessTokenProperties properties) {
        String secret = properties.secret();
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return new AccessToken(claims);
    }

    public Long getUserId() {
        return Long.parseLong(claims.getSubject());
    }

    public UserProfile getUserProfile() {
        String username = claims.get("username", String.class);
        String userEmail = claims.get("email", String.class);
        return new UserProfile(username, userEmail);
    }

    public List<String> getAuthorities() {
        List<?> authorities = claims.get("authorities", List.class);
        return authorities.stream()
                .map(String.class::cast)
                .toList();
    }

    public boolean isExpired() {
        Date now = new Date();
        return claims.getExpiration()
                .before(now);
    }

    public String serialize(AccessTokenProperties properties) {
        String secret = properties.secret();
        return Jwts.builder()
                .claims(claims)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public AccessToken rotate(AccessTokenProperties properties) {
        Long userId = getUserId();
        UserProfile userProfile = getUserProfile();
        List<String> authorities = getAuthorities();
        return new AccessToken(userId, userProfile, authorities, properties);
    }

    private Claims makeClaims(String subject, String username, String userEmail,
                              List<String> authorities, Long expirationInMilliseconds) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationInMilliseconds);

        return Jwts.claims()
                .subject(subject)
                .add("username", username)
                .add("email", userEmail)
                .add("authorities", authorities)
                .issuedAt(now)
                .expiration(expiration)
                .issuer(ISSUER)
                .build();
    }

    private void validateClaims(Claims claims) {
        if (claims == null) {
            throw new IllegalArgumentException("Claims cannot be null");
        }

        String subject = claims.getSubject();
        if (StringUtils.isBlank(subject)) {
            throw new IllegalArgumentException("Subject cannot be null or blank");
        }

        String username = claims.get("username", String.class);
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }

        String userEmail = claims.get("email", String.class);
        if (StringUtils.isBlank(userEmail)) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        List<?> authorities = claims.get("authorities", List.class);
        if (CollectionUtils.isEmpty(authorities)) {
            throw new IllegalArgumentException("Authorities cannot be null or empty");
        }

        String issuer = claims.getIssuer();
        if (!StringUtils.equals(issuer, ISSUER)) {
            throw new IllegalArgumentException("Issuer must be '" + ISSUER + "'");
        }
    }
}

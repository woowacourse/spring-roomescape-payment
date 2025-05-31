package roomescape.infrastructure;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import roomescape.domain.auth.AuthenticationInfo;
import roomescape.domain.auth.AuthenticationTokenHandler;
import roomescape.domain.user.UserRole;

@Component
public class JwtTokenHandler implements AuthenticationTokenHandler {

    private final Algorithm algorithm;
    private final long validityInMilliseconds;

    public JwtTokenHandler(
            @Value("${security.jwt.token.secret-key}") String secretKey,
            @Value("${security.jwt.token.expire-length}") long validityInMilliseconds
    ) {
        this.algorithm = Algorithm.HMAC256(secretKey);
        this.validityInMilliseconds = validityInMilliseconds;
    }

    @Override
    public String createToken(final AuthenticationInfo authenticationInfo) {
        var userId = String.valueOf(authenticationInfo.id());
        var userRole = authenticationInfo.role().name();
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return JWT.create()
                .withSubject(userId)
                .withClaim("role", userRole)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .sign(algorithm);
    }

    @Override
    public long extractId(final String token) {
        var authenticationInfo = extractAuthenticationInfo(token);
        return authenticationInfo.id();
    }

    @Override
    public AuthenticationInfo extractAuthenticationInfo(final String token) {
        DecodedJWT decodedJWT = getVerifier().verify(token);
        long id = Long.parseLong(decodedJWT.getSubject());
        UserRole role = UserRole.valueOf(decodedJWT.getClaim("role").asString());

        return new AuthenticationInfo(id, role);
    }

    @Override
    public boolean isValidToken(final String token) {
        try {
            DecodedJWT decodedJWT = getVerifier().verify(token);
            return !decodedJWT.getExpiresAt().before(new Date());
        } catch (JWTVerificationException | IllegalArgumentException e) {
            return false;
        }
    }

    private JWTVerifier getVerifier() {
        return JWT.require(algorithm).build();
    }
}


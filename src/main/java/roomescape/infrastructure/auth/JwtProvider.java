package roomescape.infrastructure.auth;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import roomescape.domain.auth.AuthJwtProvider;
import roomescape.domain.auth.TokenBody;
import roomescape.domain.member.MemberRole;

@Component
public class JwtProvider implements AuthJwtProvider {

    private final Long validityInMilliseconds;
    private final Key key;

    public JwtProvider(
            @Value("${secret.jwt-key}") final String secretKey,
            @Value("${secret.jwt-expiration}") final Long validityInMilliseconds
    ) {
        this.validityInMilliseconds = validityInMilliseconds;
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String provideToken(final String email, final MemberRole role, final String name) {
        final Date now = new Date();
        final Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .claim("role", role.name())
                .claim("name", name)

                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(validity)

                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public TokenBody extractBody(final String token) {
        return new TokenBody(Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody());
    }

    public boolean isValidToken(final String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return true;
        } catch (final JwtException e) {
            return false;
        }
    }
}

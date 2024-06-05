package roomescape.auth;

import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length}")
    private long validityInMilliseconds;

    public String createToken(final long memberId) {
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .expiration(expiration)
                .signWith(secretKey())
                .compact();
    }

    public long getMemberIdFrom(final String token) {
        return Long.parseLong(Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject()
        );
    }

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}

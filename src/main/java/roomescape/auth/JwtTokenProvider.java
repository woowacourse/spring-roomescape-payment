package roomescape.auth;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    @Value("${roomescape.security.secret}")
    private String secretKey;

    @Value("${roomescape.security.expiration}")
    private Duration expiration;

    protected JwtTokenProvider() {}

    public JwtTokenProvider(String secretKey, Duration expiration) {
        this.secretKey = secretKey;
        this.expiration = expiration;
    }

    public String createToken(final long memberId) {
        final Instant now = Instant.now();
        final Instant after = now.plus(expiration);
        final Date expiration = Date.from(after);
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

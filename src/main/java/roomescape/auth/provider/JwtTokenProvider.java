package roomescape.auth.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import roomescape.auth.domain.Token;
import roomescape.auth.exception.TokenException;
import roomescape.auth.provider.model.TokenProvider;
import roomescape.exception.RoomEscapeException;

@Tag(name = "jwt 토큰 제공자 구현체", description = "secretKey를 사용해 토큰을 암호화한다. 토큰 파싱 결과를 반환한다.")
@Component
public class JwtTokenProvider implements TokenProvider {

    private static final Date TODAY = new Date();

    private final SecretKey secretKey;
    private final long expirationTime;

    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey,
                            @Value("${jwt.expiration}") long expirationTime) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.expirationTime = expirationTime;
    }

    @Override
    public Token getAccessToken(long principle) {
        String accessToken = Jwts.builder()
                .claims(makeClaims(principle))
                .issuedAt(TODAY)
                .expiration(makeExpiration())
                .signWith(secretKey)
                .compact();

        return new Token(accessToken);
    }

    @Override
    public String resolveToken(String token) {
        return parseClaims(token).getSubject();
    }

    private Claims makeClaims(long principal) {
        return Jwts.claims()
                .subject(String.valueOf(principal))
                .build();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (UnsupportedJwtException e) {
            throw new RoomEscapeException(TokenException.NOT_SIGNED_TOKEN_EXCEPTION);
        } catch (ExpiredJwtException e) {
            throw new RoomEscapeException(TokenException.TOKEN_IS_EXPIRED_EXCEPTION);
        } catch (JwtException e) {
            throw new RoomEscapeException(TokenException.FAILED_PARSE_TOKEN_EXCEPTION);
        } catch (IllegalArgumentException e) {
            throw new RoomEscapeException(TokenException.TOKEN_IS_EMPTY_EXCEPTION);
        }
    }

    private Date makeExpiration() {
        return new Date(TODAY.getTime() + expirationTime);
    }
}

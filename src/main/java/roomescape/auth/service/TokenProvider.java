package roomescape.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import roomescape.advice.exception.ExceptionTitle;
import roomescape.advice.exception.RoomEscapeException;

@Component
public class TokenProvider {

    private final String secretKey;

    public TokenProvider(@Value("${jwt.secret-key}") String secretKey) {
        this.secretKey = secretKey;
    }

    public String createToken(Long memberId) {
        return Jwts.builder()
                .claims()
                .add("id", memberId)
                .and()
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public Long findMemberId(String token) {
        try {
            return parseId(token);
        } catch (ExpiredJwtException exception) {
            throw new RoomEscapeException("토큰이 만료되었습니다.", ExceptionTitle.AUTHENTICATION_FAILED);
        } catch (MalformedJwtException exception) {
            throw new RoomEscapeException("토큰의 형식이 잘못되었습니다.", ExceptionTitle.AUTHENTICATION_FAILED);
        } catch (InvalidClaimException exception) {
            throw new RoomEscapeException("토큰이 필요한 정보를 포함하고 있지 않습니다.", ExceptionTitle.AUTHENTICATION_FAILED);
        } catch (JwtException exception) {
            throw new RoomEscapeException("토큰이 잘못된 토큰입니다.", ExceptionTitle.AUTHENTICATION_FAILED);
        }
    }

    private Long parseId(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("id", Long.class);
    }
}

package roomescape.system.auth.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import roomescape.system.auth.jwt.dto.TokenDto;
import roomescape.system.exception.ErrorType;
import roomescape.system.exception.RoomEscapeException;

@Component
public class JwtHandler {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.access.expire-length}")
    private long accessTokenExpireTime;

    @Value("${security.jwt.token.refresh.expire-length}")
    private long refreshTokenExpireTime;

    public TokenDto createToken(Long memberId) {
        Date date = new Date();
        Date accessTokenExpiredAt = new Date(date.getTime() + accessTokenExpireTime);
        Date refreshTokenExpiredAt = new Date(date.getTime() + refreshTokenExpireTime);

        String accessToken = Jwts.builder()
                .claim("memberId", memberId)
                .setIssuedAt(date)
                .setExpiration(accessTokenExpiredAt)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();

        String refreshToken = Jwts.builder()
                .setIssuedAt(date)
                .setExpiration(refreshTokenExpiredAt)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();

        return new TokenDto(accessToken, refreshToken);
    }

    public Long getMemberIdFromToken(String token) {
        validateToken(token);

        return Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token)
                .getBody()
                .get("memberId", Long.class);
    }

    public Long getMemberIdFromTokenWithNotValidate(String token) {
        return Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token)
                .getBody()
                .get("memberId", Long.class);
    }

    public void validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new RoomEscapeException(ErrorType.EXPIRED_TOKEN, HttpStatus.UNAUTHORIZED);
        } catch (UnsupportedJwtException e) {
            throw new RoomEscapeException(ErrorType.UNSUPPORTED_TOKEN, HttpStatus.UNAUTHORIZED);
        } catch (MalformedJwtException e) {
            throw new RoomEscapeException(ErrorType.MALFORMED_TOKEN, HttpStatus.UNAUTHORIZED);
        } catch (SignatureException e) {
            throw new RoomEscapeException(ErrorType.INVALID_SIGNATURE_TOKEN, HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            throw new RoomEscapeException(ErrorType.ILLEGAL_TOKEN, HttpStatus.UNAUTHORIZED);
        }
    }
}

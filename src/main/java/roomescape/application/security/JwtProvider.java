package roomescape.application.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Component;
import roomescape.domain.member.Member;

@Component
public class JwtProvider {
    private static final String SECRET_KEY = "hellowootecoworldhihowareyouiamfinethankyouandyou";
    private static final String ROLE_CLAIM_KEY = "role";
    private static final String NAME_CLAIM_KEY = "name";

    public String encode(Member user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim(ROLE_CLAIM_KEY, user.getRoleAsString())
                .claim(NAME_CLAIM_KEY, user.getName())
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .compact();
    }

    public Claims verifyToken(String token) {
        try {
            return parseClaims(token);
        } catch (ExpiredJwtException e) {
            throw new JwtException("기한이 만료된 JWT 토큰입니다.", e);
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            throw new JwtException("JWT 토큰 구성이 올바르지 않습니다.", e);
        } catch (SignatureException e) {
            throw new JwtException("JWT 토큰 검증에 실패하였습니다.", e);
        }
    }

    private Claims parseClaims(String token) {
        token = token.replace("token=", "");
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

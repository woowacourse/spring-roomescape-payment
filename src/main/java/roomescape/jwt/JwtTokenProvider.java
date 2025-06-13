package roomescape.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import roomescape.entity.Member;
import roomescape.exception.custom.UnauthorizedException;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.expire-duration}")
    private int EXPIRE_DURATION;

    public String createTokenByMember(Member member) {
        log.info("JWT 토큰 생성 시작 - memberId: {}, name: {}, role: {}",
                member.getId(), member.getName(), member.getRole());

        Date now = new Date();
        Date expireDate = new Date(now.getTime() + EXPIRE_DURATION);

        String token = Jwts.builder()
                .setSubject(member.getId().toString())
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .claim("name", member.getName())
                .claim("role", member.getRole())
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .compact();

        log.info("JWT 토큰 생성 완료 - memberId: {}, expireDate: {}", member.getId(), expireDate);
        return token;
    }

    public Claims getClaimsFromToken(String token) {
        log.info("JWT 토큰에서 Claims 추출 시작");

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            log.info("JWT 토큰에서 Claims 추출 완료 - subject: {}", claims.getSubject());
            return claims;
        } catch (ExpiredJwtException e) {
            log.error("JWT 토큰 만료 - token: {}", token, e);
            throw new UnauthorizedException("토큰이 만료되었습니다.");
        } catch (JwtException | IllegalArgumentException e) {
            log.error("유효하지 않은 JWT 토큰 - token: {}", token, e);
            throw new UnauthorizedException("유효하지 않은 토큰입니다.");
        }
    }
}

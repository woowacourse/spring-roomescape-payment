package roomescape.common.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import roomescape.common.exception.LoginException;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtTokenContainer {

    private static final int TOKEN_EXPIRATION_MINUTES = 30;

    private final SecretKey secretKey;

    public JwtTokenContainer(@Value("${security.jwt.token.secret-key}") final String key) {
        this.secretKey = Keys.hmacShaKeyFor(key.getBytes());
    }

    public String createJwtToken(final Member member, final LocalDateTime now) {
        Date expirationDate = createExpirationDate(now);

        return Jwts.builder()
                .subject(member.getId().toString())
                .claim("role", member.getRole())
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    public void validateToken(final String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            throw new LoginException("만료된 토큰입니다.");
        } catch (JwtException e) {
            throw new LoginException("올바르지 않은 토큰 형태입니다.");
        }
    }

    public Long getMemberId(final String token) {
        try {
            String id = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return Long.parseLong(id);
        } catch (JwtException e) {
            throw new LoginException("올바르지 않은 토큰 형태입니다.");
        }
    }

    public Role getMemberRole(final String token) {
        try {
            String role = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("role").toString();
            return Role.findRole(role);
        } catch (JwtException e) {
            throw new LoginException("올바르지 않은 토큰 형태입니다.");
        }
    }

    private Date createExpirationDate(final LocalDateTime now) {
        LocalDateTime expirationTime = now.plusMinutes(TOKEN_EXPIRATION_MINUTES);
        return Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}

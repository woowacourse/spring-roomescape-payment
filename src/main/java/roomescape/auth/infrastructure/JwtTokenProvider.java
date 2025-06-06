package roomescape.auth.infrastructure;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import roomescape.auth.domain.AuthRole;
import roomescape.auth.domain.AuthTokenProvider;

@Slf4j
@Component
public class JwtTokenProvider implements AuthTokenProvider {

    private final SecretKey secretKey;

    @Value("${security.jwt.access-token.validity-in-milliseconds}")
    private long validityInMilliseconds;

    public JwtTokenProvider(@Value("${security.jwt.access-token.secret-key}") final String secretKeyValue) {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyValue.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(final String principal, final AuthRole role) {
        Claims claims = Jwts.claims().subject(principal).build();
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        String token = Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .claim("role", role.name())
                .signWith(secretKey)
                .compact();

        log.info("JWT 생성 완료: subject={}, role={}, 만료일시={}", principal, role.name(), validity);
        return token;
    }

    public String getPrincipal(final String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        String subject = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        log.debug("JWT 파싱 완료 - subject 추출: {}", subject);
        return subject;
    }

    public Instant getExpiration(final String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        Instant expiration = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .toInstant();

        log.debug("JWT 파싱 완료 - 만료 시간 추출: {}", expiration);
        return expiration;
    }

    public AuthRole getRole(final String token) {
        if (token == null || token.isEmpty()) {
            return AuthRole.GUEST;
        }

        String role = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);

        log.debug("JWT 파싱 완료 - 사용자 역할 추출: {}", role);
        return AuthRole.valueOf(role);
    }

    public boolean isValidToken(final String token) {
        if (token == null || token.isEmpty()) {
            log.warn("유효성 검사 실패: 토큰이 비어있음");
            return false;
        }

        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            log.warn("토큰 형식 오류: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("토큰 만료됨: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("지원하지 않는 JWT 형식: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("JWT 서명 검증 실패: {}", e.getMessage());
        } catch (JwtException e) {
            log.warn("기타 JWT 파싱 예외 발생: {}", e.getMessage());
        }

        return false;
    }
}

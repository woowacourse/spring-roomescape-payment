package roomescape.infrastructure;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import org.springframework.stereotype.Component;
import roomescape.domain.Role;

@Component
public class TokenGenerator {

    private static final String ADMIN = Role.ADMIN.name();
    private static final String COOKIE_NAME = "token";
    private static final String ROLE = "role";

    private final String secretKey = "secret-token-test";
    private final long validityInMilliseconds = 3600000;

    public String createToken(String payload, String role) {
        Claims claims = Jwts.claims().setSubject(payload);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .claim(ROLE, role)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String getPayload(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public String getTokenFromCookies(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> COOKIE_NAME.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new IllegalArgumentException("로그인 토큰이 없습니다"));
    }

    public void validateTokenRole(String token) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        boolean isAdmin = ADMIN.equals(claims.getBody().get(ROLE));

        if (!isAdmin) {
            throw new JwtException("해당 기능에 접근하려면 관리자 권한이 필요합니다.");
        }
    }

    public String getCookieName() {
        return COOKIE_NAME;
    }
}
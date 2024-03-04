package auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

public class JwtUtils {
    @Value("${roomescape.auth.jwt.secret}")
    private String secretKey;

    public String createToken(String subject, Map<String, Object> claims) {

        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public String extractSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public String extractClaim(String token, String key) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody().get(key).toString();
    }
}

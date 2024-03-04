package auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.Map;

public class JwtUtils {
    public String createToken(String subject, Map<String, Object> claims) {

        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .signWith(Keys.hmacShaKeyFor("Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=".getBytes()))
                .compact();
    }

    public String extractSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor("Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=".getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public String extractClaim(String token, String key) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor("Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=".getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody().get(key).toString();
    }
}

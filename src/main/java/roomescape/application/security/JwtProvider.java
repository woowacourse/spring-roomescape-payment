package roomescape.application.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.exception.AuthenticationException;
import roomescape.web.support.JwtTokenProperties;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private static final String ROLE_CLAIM_KEY = "role";
    private static final String NAME_CLAIM_KEY = "name";

    private final JwtTokenProperties jwtTokenProperties;

    public String encode(Member user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim(ROLE_CLAIM_KEY, user.getRoleAsString())
                .claim(NAME_CLAIM_KEY, user.getName())
                .signWith(Keys.hmacShaKeyFor(jwtTokenProperties.secret().getBytes()))
                .compact();
    }

    public Long extractId(String token) {
        return Long.valueOf(extractPayload(token).getSubject());
    }

    public Role extractRole(String token) {
        return Role.of(extractPayload(token).get(ROLE_CLAIM_KEY, String.class));
    }

    public String extractName(String token) {
        return extractPayload(token).get(NAME_CLAIM_KEY, String.class);
    }

    private Claims extractPayload(String token) {
        try {
            token = token.replace("token=", "");

            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(jwtTokenProperties.secret().getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new AuthenticationException();
        }
    }

}

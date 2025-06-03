package roomescape.member.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import roomescape.common.exception.AuthenticationException;
import roomescape.common.exception.AuthorizationException;
import roomescape.common.exception.ErrorCode;
import roomescape.member.auth.vo.MemberInfo;
import roomescape.member.domain.MemberName;
import roomescape.member.domain.Role;

@Component
public class JwtTokenExtractor {

    @Value("${jwt.key}")
    private String SECRET_KEY;

    public String extractTokenFromCookie(Cookie[] cookies) {
        if (cookies == null) {
            throw new AuthorizationException("로그인이 필요합니다.", ErrorCode.MUST_BE_MEMBER);
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                return cookie.getValue();
            }
        }

        throw new AuthorizationException("로그인이 필요합니다.", ErrorCode.MUST_BE_MEMBER);
    }
    
    public MemberInfo extractMemberInfoFromToken(final String token) {
        final Claims claims = extractClaimsFromToken(token);
        final Long id = Long.valueOf(claims.getSubject());
        final String name = claims.get("name").toString();
        final String email = claims.get("email").toString();
        final Role role = Role.valueOf(claims.get("role").toString());

        return new MemberInfo(id, name, email, role);
    }

    public Role extractRoleFromToken(final String token) {
        final Claims claims = extractClaimsFromToken(token);
        final String role = claims.get("role").toString();

        return Role.valueOf(role);
    }

    public Long extractMemberIdFromToken(String token) {
        Long id = Long.valueOf(extractClaimsFromToken(token).getSubject());
        return id;
    }

    public MemberName extractMemberNameFromToken(String token) {
        String name = extractClaimsFromToken(token)
                .get("name").toString();
        return MemberName.from(name);
    }

    private Claims extractClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (MalformedJwtException e) {
            throw new AuthenticationException("잘못된 토큰입니다.", ErrorCode.INVALID_AUTH_INFO);
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException("만료된 토큰입니다.", ErrorCode.EXPIRED_TOKEN);
        }
    }
}

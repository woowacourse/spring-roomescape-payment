package roomescape.auth.infrastructure;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import roomescape.auth.application.LoginMember;
import roomescape.auth.config.JwtProperties;
import roomescape.auth.exception.TokenCreationException;
import roomescape.auth.exception.UnauthorizedException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;

@Slf4j
@Component
public class JwtTokenProvider implements TokenProvider {

    private static final String CLAIM_NAME = "name";
    private static final String CLAIM_ROLE = "role";

    private final Clock clock;
    private final SecretKey secretKey;
    private final JwtParser jwtParser;
    private final Duration validity;

    public JwtTokenProvider(final JwtProperties jwtProperties, final Clock clock) {
        this.clock = clock;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
        this.jwtParser = Jwts.parser()
                .verifyWith(secretKey)
                .build();
        this.validity = Duration.ofMillis(jwtProperties.getExpireLength());
    }

    @Override
    public String createToken(final Member member) {
        Instant now = clock.instant();
        try {
            return buildToken(member, now);
        } catch (JwtException e) {
            log.error("[토큰 생성] 토큰 생성 과정 실패 - memberId: {}, error: {}", member.getId(), e.getMessage());
            throw new TokenCreationException("로그인 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
        }
    }

    @Override
    public LoginMember extractLoginMember(final String token) {
        Claims claims = parseAllClaims(token);
        return new LoginMember(
                Long.valueOf(claims.getSubject()),
                claims.get(CLAIM_NAME, String.class),
                MemberRole.valueOf(claims.get(CLAIM_ROLE, String.class))
        );
    }

    private String buildToken(Member member, Instant now) {
        return Jwts.builder()
                .claims(buildClaims(member))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(validity)))
                .signWith(secretKey)
                .compact();
    }

    private Claims buildClaims(final Member member) {
        return Jwts.claims()
                .subject(member.getId().toString())
                .add(CLAIM_NAME, member.getName())
                .add(CLAIM_ROLE, member.getRole().name())
                .build();
    }

    private Claims parseAllClaims(final String token) {
        try {
            return jwtParser.parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("[JWT 만료] 사용자 재로그인 필요");
            throw new UnauthorizedException("인증이 만료되었습니다. 다시 로그인 해주세요.");
        } catch (JwtException | IllegalArgumentException e) {
            log.error("[JWT 검증 실패] 원인: {}", e.getClass().getSimpleName());
            throw new UnauthorizedException("인증에 실패했습니다");
        }
    }
}

package roomescape.auth.infrastructure;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
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
            log.error("토큰 생성 실패 - 회원 ID: {}, error: {}", member.getId(), e.getMessage());
            throw new TokenCreationException();
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
        } catch (JwtException e) {
            log.error("JWT 토큰 처리 중 오류 발생. token: {}, error: {}", token, e.getMessage());
            throw new UnauthorizedException(convertToErrorMessage(e));
        } catch (IllegalArgumentException e) {
            log.error("잘못된 토큰 형식. token: {}, error: {}", token, e.getMessage());
            throw new UnauthorizedException("토큰이 제공되지 않았거나 잘못된 값입니다.");
        }
    }

    private String convertToErrorMessage(final JwtException e) {
        if (e instanceof ExpiredJwtException) {
            return "토큰이 만료되었습니다.";
        }
        if (e instanceof UnsupportedJwtException) {
            return "지원하지 않는 토큰 형식입니다.";
        }
        if (e instanceof MalformedJwtException) {
            return "잘못된 토큰 형식입니다.";
        }
        if (e instanceof SignatureException) {
            return "토큰 서명이 올바르지 않습니다.";
        }
        return "토큰 검증에 실패했습니다.";
    }
}

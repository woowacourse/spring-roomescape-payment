package roomescape.auth.token;

import static java.lang.System.currentTimeMillis;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import javax.crypto.SecretKey;
import roomescape.member.model.MemberRole;

public class TokenProvider {

    private final SecretKey tokenSecretKey;
    private final long tokenExpirationPeriod;

    public TokenProvider(final SecretKey tokenSecretKey, final long tokenExpirationPeriod) {
        this.tokenSecretKey = tokenSecretKey;
        this.tokenExpirationPeriod = tokenExpirationPeriod;
    }

    public String createToken(final Long memberId, final MemberRole role) {
        final Claims claim = createClaims(memberId, role);
        return Jwts.builder()
                .claims(claim)
                .signWith(tokenSecretKey)
                .compact();
    }

    private Claims createClaims(final Long memberId, final MemberRole role) {
        Date expiredTime = new Date(new Date().getTime() + tokenExpirationPeriod);
        return Jwts.claims()
                .subject(memberId.toString())
                .issuedAt(new Date(currentTimeMillis()))
                .expiration(expiredTime)
                .add("role", role.name())
                .build();
    }

    public Claims getTokenClaims(final String token) {
        return Jwts.parser()
                .verifyWith(tokenSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

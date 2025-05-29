package roomescape.infrastructure.security;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private final Clock clock;

    public JwtProvider(final JwtProperties jwtProperties, final Clock clock) {
        this.jwtProperties = jwtProperties;
        this.clock = clock;
    }

    public AccessToken issue(final Long identifier) {
        final TokenIssueRequest tokenIssueRequest = getTokenIssueRequest(identifier);
        return AccessToken.create(tokenIssueRequest);
    }

    private TokenIssueRequest getTokenIssueRequest(final Long identifier) {
        final Instant currentInstant = clock.instant();
        final Instant expireInstant = currentInstant.plus(jwtProperties.getExpireDuration());
        final Date currentDate = Date.from(currentInstant);
        final Date expireDate = Date.from(expireInstant);
        return new TokenIssueRequest(
                currentDate,
                expireDate,
                identifier,
                jwtProperties.getSecretKey()
        );
    }

    public Long extractIdentifier(final AccessToken accessToken) {
        return accessToken.extractMemberId(jwtProperties.getSecretKey());
    }
}

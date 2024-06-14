package roomescape.service;

import io.jsonwebtoken.Claims;
import roomescape.model.Member;

public interface TokenProvider {
    String createToken(final Member member);

    Claims getPayload(final String token);
}

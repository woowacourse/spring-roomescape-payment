package roomescape.auth.infrastructure;

import roomescape.member.domain.Member;

public interface TokenProvider {

    String createToken(final Member member);

    String extractPrincipal(final String token);
}

package roomescape.domain.auth;

import roomescape.domain.member.MemberRole;

public interface AuthJwtProvider {

    String provideToken(final String email, final MemberRole role, final String name);
}

package roomescape.security.authentication;

import roomescape.domain.member.Member;
import roomescape.exception.UnauthorizedException;

public class AnonymousAuthentication implements Authentication {

    @Override
    public Member getPrincipal() {
        throw new UnauthorizedException();
    }

    @Override
    public boolean isNotAdmin() {
        throw new UnauthorizedException();
    }
}

package roomescape.security.authentication;

import roomescape.domain.member.Member;

public interface Authentication {

    Member getPrincipal();

    boolean isNotAdmin();
}

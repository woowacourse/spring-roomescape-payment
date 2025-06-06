package roomescape.auth.infrastructure;

import roomescape.auth.application.LoginMember;
import roomescape.member.domain.Member;

public interface TokenProvider {

    String createToken(final Member member);

    LoginMember extractLoginMember(final String token);
}

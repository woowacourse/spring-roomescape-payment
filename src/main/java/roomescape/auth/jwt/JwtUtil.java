package roomescape.auth.jwt;

import roomescape.auth.AuthToken;
import roomescape.auth.LoginInfo;
import roomescape.business.model.entity.Member;

public interface JwtUtil {

    AuthToken createToken(Member member);

    LoginInfo validateAndResolveToken(AuthToken token);
}

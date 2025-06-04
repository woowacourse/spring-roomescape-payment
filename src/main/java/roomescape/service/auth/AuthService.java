package roomescape.service.auth;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.dto.request.LoginRequest;
import roomescape.service.helper.MemberHelper;

import javax.naming.AuthenticationException;

@RequiredArgsConstructor
@Service
public class AuthService {

    private static final String SESSION_KEY = "id";
    private static final int SESSION_TIMEOUT_SECOND = 60 * 60;

    private final MemberHelper memberHelper;

    @Transactional
    public void login(final LoginRequest loginRequest, final HttpSession session) throws AuthenticationException {
        final Member member = memberHelper.getMemberByEmail(loginRequest.email());

        if (!member.getPassword().matches(loginRequest.password())) {
            throw new AuthenticationException("[ERROR] 비밀번호가 일치하지 않습니다.");
        }

        session.setAttribute(SESSION_KEY, member.getId());
        session.setMaxInactiveInterval(SESSION_TIMEOUT_SECOND);
        member.updateSessionId(session.getId());
    }

    @Transactional
    public void logout(final HttpSession session) {
        session.invalidate();
    }
}

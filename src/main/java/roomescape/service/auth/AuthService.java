package roomescape.service.auth;

import static roomescape.global.exception.authentication.AuthenticationErrorStatus.PASSWORD_DOES_NOT_MATCH;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.domain.member.Member;
import roomescape.dto.request.LoginRequest;
import roomescape.global.PasswordEncoder;
import roomescape.global.exception.authentication.AuthenticationException;
import roomescape.service.member.MemberService;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    public Long authenticate(final LoginRequest loginRequest) {
        final Member member = memberService.getMemberByEmail(loginRequest.email());

        if (!passwordEncoder.matches(loginRequest.password(), member.getPassword())) {
            throw new AuthenticationException(PASSWORD_DOES_NOT_MATCH);
        }
        return member.getId();
    }

    public void updateSessionIdByMemberId(final Long memberId, final String sessionId) {
        final Member member = memberService.getMemberById(memberId);

        member.updateSessionId(sessionId);
    }
}

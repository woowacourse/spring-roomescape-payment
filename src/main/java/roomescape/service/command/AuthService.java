package roomescape.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Member;
import roomescape.dto.business.AccessTokenContent;
import roomescape.dto.request.LoginRequest;
import roomescape.dto.response.AccessTokenResponse;
import roomescape.exception.LoginFailException;
import roomescape.exception.NotFoundException;
import roomescape.service.query.MemberQueryService;
import roomescape.utility.JwtTokenProvider;

@Service
@Transactional
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberQueryService memberQueryService;

    public AuthService(
            JwtTokenProvider jwtTokenProvider,
            MemberQueryService memberQueryService
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberQueryService = memberQueryService;
    }

    public AccessTokenResponse login(LoginRequest loginRequest) {
        Member member = getMemberByEmail(loginRequest.email());
        validatePasswordForLogin(member, loginRequest.password());
        String accessToken = jwtTokenProvider.createAccessToken(
                new AccessTokenContent(member.getId(), member.getRole(), member.getName()));
        return new AccessTokenResponse(accessToken);
    }

    private void validatePasswordForLogin(Member member, String password) {
        if (!member.isEqualPassword(password)) {
            throw new LoginFailException("로그인 정보가 올바르지 않습니다.");
        }
    }

    public Member getMemberByEmail(String email) {
        try {
            return memberQueryService.getMemberByEmail(email);
        } catch (NotFoundException exception) {
            throw new LoginFailException("이메일에 해당하는 회원이 존재하지 않습니다.");
        }
    }
}

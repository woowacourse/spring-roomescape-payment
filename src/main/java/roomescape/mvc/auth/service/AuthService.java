package roomescape.mvc.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.LoginFailException;
import roomescape.exception.NotFoundException;
import roomescape.mvc.auth.dto.AccessTokenContent;
import roomescape.mvc.auth.request.LoginRequest;
import roomescape.mvc.auth.response.AccessTokenResponse;
import roomescape.mvc.member.domain.Member;
import roomescape.mvc.member.service.MemberQueryService;
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

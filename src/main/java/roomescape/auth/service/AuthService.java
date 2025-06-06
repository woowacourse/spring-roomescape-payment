package roomescape.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.service.out.TokenProvider;
import roomescape.auth.web.controller.request.LoginRequest;
import roomescape.auth.web.controller.response.MemberNameResponse;
import roomescape.auth.web.exception.NotAuthorizationException;
import roomescape.global.exception.NotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.service.MemberQueryService;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final TokenProvider tokenProvider;
    private final MemberQueryService memberQueryService;

    public String login(LoginRequest request) {
        try {
            Member member = memberQueryService.getMember(request.email(), request.password());
            return tokenProvider.issue(member);
        } catch (NotFoundException e) {
            throw new NotAuthorizationException("로그인 정보가 올바르지 않습니다.");
        }
    }

    @Transactional(readOnly = true)
    public MemberNameResponse checkLogin(Long memberId) {
        try {
            Member member = memberQueryService.getMember(memberId);
            return new MemberNameResponse(member.getName());
        } catch (NotFoundException e) {
            throw new NotAuthorizationException("로그인 정보가 유효하지 않습니다.");
        }
    }

    public boolean isAdmin(String token) {
        Role role = tokenProvider.getRole(token);
        return role.isAdmin();
    }

    public Long getMemberId(String token) {
        return tokenProvider.getMemberId(token);
    }
}

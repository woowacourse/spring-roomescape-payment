package roomescape.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.service.out.TokenProvider;
import roomescape.auth.web.controller.request.LoginRequest;
import roomescape.auth.web.controller.response.MemberNameResponse;
import roomescape.auth.web.exception.NotAuthorizationException;
import roomescape.global.exception.NotFoundException;
import roomescape.logging.utils.MaskingUtil;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.service.MemberQueryService;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final TokenProvider tokenProvider;
    private final MemberQueryService memberQueryService;

    public String login(LoginRequest request) {
        try {
            log.info("로그인 요청 | 이메일 = {}", MaskingUtil.maskEmail(request.email()));
            Member member = memberQueryService.getMember(request.email(), request.password());
            return tokenProvider.issue(member);
        } catch (NotFoundException e) {
            log.warn("로그인 실패 | 이메일 = {} | 예외 메시지 = {}", MaskingUtil.maskEmail(request.email()), e.getMessage());
            throw new NotAuthorizationException("로그인 정보가 올바르지 않습니다.");
        }
    }

    @Transactional(readOnly = true)
    public MemberNameResponse checkLogin(Long memberId) {
        try {
            log.info("로그인 정보 확인 요청 | memberId = {}", memberId);
            Member member = memberQueryService.getMember(memberId);
            return new MemberNameResponse(member.getName());
        } catch (NotFoundException e) {
            log.warn("로그인 실패 | memberId = {} | 예외 메시지 = {}", memberId, e.getMessage());
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

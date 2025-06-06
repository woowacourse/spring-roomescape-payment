package roomescape.auth.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.auth.application.AuthService;
import roomescape.auth.application.LoginMember;
import roomescape.member.domain.Member;
import roomescape.member.service.MemberService;

@Component
@RequiredArgsConstructor
public class AuthCheckInterceptor implements HandlerInterceptor {

    private final AuthService authService;
    private final MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (request.getCookies() == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        LoginMember loginMember = authService.extractMemberByRequest(request);
        if (loginMember.isNotAdmin()) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return false;
        }

        if (isNotAdminMember(loginMember)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        return true;
    }

    private boolean isNotAdminMember(LoginMember loginMember) {
        Member member = memberService.getMemberById(loginMember.getId());
        return member.isNotAdmin();
    }
}

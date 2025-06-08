package roomescape.global;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.exception.ErrorCode;
import roomescape.exception.UnauthorizedException;
import roomescape.member.domain.Member;
import roomescape.member.service.MemberService;


@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String SESSION_KEY = "id";
    private final MemberService memberService;

    public AuthInterceptor(final MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
                             final Object handler) {
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/admin")) {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute(SESSION_KEY) == null) {
                throw new UnauthorizedException(ErrorCode.LOGIN_NEEDED);
            }
            Long memberId = (Long) session.getAttribute(SESSION_KEY);
            Member member = memberService.getMemberById(memberId);
            if (member.isAdmin()) {
                return true;
            }
            throw new UnauthorizedException(ErrorCode.ADMIN_ONLY);
        }
        return true;
    }
}

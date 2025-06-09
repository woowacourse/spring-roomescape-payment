package roomescape.global;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.global.dto.SessionMember;
import roomescape.global.exception.AccessDeniedException;
import roomescape.global.exception.AuthenticationException;
import roomescape.member.domain.MemberRole;

@Slf4j
public class AdminOnlyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null) {
            log.warn("[AUTH] 세션 없음 - 요청 URI: {}, IP: {}, Method: {}",
                    request.getRequestURI(),
                    request.getRemoteAddr(),
                    request.getMethod());
            throw new AuthenticationException("로그인이 필요합니다.");
        }
        SessionMember sessionMember = (SessionMember) session.getAttribute("LOGIN_MEMBER");
        if (sessionMember == null) {
            log.warn("[AUTH] 로그인 정보 없음 - 요청 URI: {}, IP: {}, Method: {}, SessionId: {}",
                    request.getRequestURI(),
                    request.getRemoteAddr(),
                    request.getMethod(),
                    session.getId());
            throw new AuthenticationException("로그인이 필요합니다.");
        }
        if (sessionMember.role() != MemberRole.ADMIN) {
            log.warn("[AUTH] 관리자 권한 아님 - 요청 URI: {}, IP: {}, Method: {}, 사용자 ID: {}, Role: {}",
                    request.getRequestURI(),
                    request.getRemoteAddr(),
                    request.getMethod(),
                    sessionMember.id(),
                    sessionMember.role());
            throw new AccessDeniedException("관리자가 아닙니다.");
        }
        return true;
    }
}

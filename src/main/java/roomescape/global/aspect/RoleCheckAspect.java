package roomescape.global.aspect;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import roomescape.global.annotation.CheckRole;
import roomescape.global.dto.SessionMember;
import roomescape.global.exception.AccessDeniedException;
import roomescape.global.exception.AuthenticationException;
import roomescape.member.domain.MemberRole;

@Aspect
@Component
@Slf4j
public class RoleCheckAspect {

    private final HttpSession session;

    public RoleCheckAspect(final HttpSession session) {
        this.session = session;
    }

    @Before("@annotation(checkRole)")
    public void checkUserRole(CheckRole checkRole) {
        MemberRole requiredRole = checkRole.value();
        if (session == null) {
            log.warn("[AUTH-AOP] 세션 없음 - 권한: {}", requiredRole);
            throw new AuthenticationException("로그인이 필요합니다.");
        }
        SessionMember sessionMember = (SessionMember) session.getAttribute("LOGIN_MEMBER");
        if (sessionMember == null) {
            log.warn("[AUTH-AOP] 로그인 정보 없음 - 권한: {}, SessionId: {}",
                    requiredRole, session.getId());
            throw new AuthenticationException("로그인이 필요합니다.");
        }
        if (sessionMember.role() != requiredRole) {
            log.warn("[AUTH-AOP] 역할 불일치 - 사용자 ID: {}, 현재 역할: {}, 요구 역할: {}",
                    sessionMember.id(), sessionMember.role(), requiredRole);
            throw new AccessDeniedException("권한이 없습니다.");
        }
    }
}

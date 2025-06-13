package roomescape.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.auth.annotation.RequiredAdmin;
import roomescape.auth.service.JwtTokenHandler;
import roomescape.common.exception.custom.AuthenticationException;
import roomescape.member.domain.Role;

@Slf4j
@Component
public class AdminInterceptor implements HandlerInterceptor {

    private final JwtTokenHandler jwtTokenHandler;

    public AdminInterceptor(final JwtTokenHandler jwtTokenHandler) {
        this.jwtTokenHandler = jwtTokenHandler;
    }

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        if (canAccessAnyone(handlerMethod)) {
            return true;
        }
        return hasAdminRole(request);
    }

    private boolean canAccessAnyone(final HandlerMethod handlerMethod) {
        return handlerMethod.getMethodAnnotation(RequiredAdmin.class) == null;
    }

    private boolean hasAdminRole(final HttpServletRequest request) {
        String token = jwtTokenHandler.extractTokenValue(request);
        Role role = jwtTokenHandler.getRole(token);

        if (role == Role.ADMIN) {
            return true;
        }
        log.warn("관리자 권한 아님: role={}, URI={}", role, request.getRequestURI());
        throw new AuthenticationException("권한이 존재하지 않습니다.");
    }
}

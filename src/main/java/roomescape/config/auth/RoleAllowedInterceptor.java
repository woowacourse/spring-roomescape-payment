package roomescape.config.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.controller.login.AuthCookieHandler;
import roomescape.domain.member.MemberRole;
import roomescape.exception.login.AccessDeniedException;
import roomescape.service.login.LoginService;

import java.lang.reflect.Method;

public class RoleAllowedInterceptor implements HandlerInterceptor {
    private final LoginService loginService;
    private final AuthCookieHandler authCookieHandler;

    public RoleAllowedInterceptor(LoginService loginService, AuthCookieHandler authCookieHandler) {
        this.loginService = loginService;
        this.authCookieHandler = authCookieHandler;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        Method method = handlerMethod.getMethod();
        if (method.isAnnotationPresent(RoleAllowed.class)) {
            checkRoleAccess(method, request);
        }
        return true;
    }

    private void checkRoleAccess(Method method, HttpServletRequest request) {
        RoleAllowed annotation = method.getAnnotation(RoleAllowed.class);
        MemberRole roleAllowed = annotation.value();

        String token = authCookieHandler.getToken(request.getCookies());
        MemberRole currentRole = loginService.findMemberRoleByToken(token);

        if (currentRole.isLowerThan(roleAllowed)) {
            throw new AccessDeniedException();
        }
    }
}

package roomescape.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.exception.AuthorizationException;
import roomescape.model.Role;
import roomescape.service.AuthService;

public class CheckRoleInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    public CheckRoleInterceptor(final AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
            throws Exception {
        Role role = authService.findRoleByCookie(request.getCookies());
        if (isAdmin(role)) {
            return true;
        }
        throw new AuthorizationException();
    }

    private boolean isAdmin(Role role) {
        return role == Role.ADMIN;
    }
}

package roomescape.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.common.exception.LoginException;
import roomescape.common.util.JwtTokenContainer;
import roomescape.common.util.TokenCookieManager;
import roomescape.member.domain.Role;

public class AdminInterceptor implements HandlerInterceptor {

    private final TokenCookieManager tokenCookieManager;
    private final JwtTokenContainer jwtTokenContainer;

    public AdminInterceptor(final TokenCookieManager tokenCookieManager, final JwtTokenContainer jwtTokenContainer) {
        this.tokenCookieManager = tokenCookieManager;
        this.jwtTokenContainer = jwtTokenContainer;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
    throws Exception {
        try {
            String token = tokenCookieManager.extractTokenFromCookie(request);
            Role memberRole = jwtTokenContainer.getMemberRole(token);
            if (memberRole.equals(Role.ADMIN)) {
                return true;
            }
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return false;
        } catch (LoginException e) {
            return redirectedToLogin(response);
        }
    }

    private boolean redirectedToLogin(final HttpServletResponse response) throws IOException {
        response.sendRedirect("/login");
        return false;
    }
}

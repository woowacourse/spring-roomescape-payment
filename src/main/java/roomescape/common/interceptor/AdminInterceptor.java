package roomescape.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.common.exception.LoginException;
import roomescape.common.util.JwtTokenContainer;
import roomescape.common.util.TokenCookieManager;
import roomescape.member.domain.Role;

import java.io.IOException;

@RequiredArgsConstructor
public class AdminInterceptor implements HandlerInterceptor {

    private final TokenCookieManager tokenCookieManager;
    private final JwtTokenContainer jwtTokenContainer;

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

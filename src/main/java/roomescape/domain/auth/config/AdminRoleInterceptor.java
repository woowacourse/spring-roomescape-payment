package roomescape.domain.auth.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.domain.auth.exception.MissingTokenException;
import roomescape.domain.auth.service.AuthService;

@RequiredArgsConstructor
public class AdminRoleInterceptor implements HandlerInterceptor {

    private static final String TOKEN = "token";

    private final AuthService authService;

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler) throws Exception {

        final String token = extractTokenFromCookies(request.getCookies());
        authService.validateAdminByToken(token);
        return true;
    }

    private String extractTokenFromCookies(final Cookie[] cookies) {
        if (cookies == null) {
            throw new MissingTokenException("토큰이 존재하지 않습니다.");
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(TOKEN)) {
                return cookie.getValue();
            }
        }

        throw new MissingTokenException("토큰이 존재하지 않습니다.");
    }
}

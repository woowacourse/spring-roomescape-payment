package roomescape.web.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.application.security.JwtProvider;
import roomescape.domain.member.Role;
import roomescape.exception.AuthenticationException;
import roomescape.exception.AuthorizationException;

@RequiredArgsConstructor
public class AdminHandlerInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Cookie tokenCookie = extractCookie(request, "token");
        String token = tokenCookie.getValue();
        Role role = jwtProvider.extractRole(token);
        if (!role.isAdmin()) {
            throw new AuthorizationException();
        }
        return true;
    }

    private Cookie extractCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return new Cookie("token", "");
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny()
                .orElseThrow(AuthenticationException::new);
    }

}

package roomescape.web.support;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.application.security.JwtProvider;
import roomescape.domain.member.Role;
import roomescape.exception.AuthenticationException;
import roomescape.exception.AuthorizationException;

@Component
@RequiredArgsConstructor
public class AdminHandlerInterceptor implements HandlerInterceptor {
    private static final String TOKEN_LITERAL = "token";
    private static final Cookie EMPTY_TOKEN_COOKIE = new Cookie(TOKEN_LITERAL, "");

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Cookie tokenCookie = extractCookie(request, TOKEN_LITERAL);
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
            return EMPTY_TOKEN_COOKIE;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny()
                .orElseThrow(AuthenticationException::new);
    }
}

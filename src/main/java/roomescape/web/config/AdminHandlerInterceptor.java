package roomescape.web.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.application.security.JwtProvider;
import roomescape.domain.member.Role;
import roomescape.exception.member.AuthorizationFailureException;

@Component
@RequiredArgsConstructor
public class AdminHandlerInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        Optional<Cookie> cookie = extractCookie(request, "token");

        if (cookie.isEmpty()) {
            response.sendRedirect("/login");
            return false;
        }

        String token = cookie.get().getValue();
        Claims claims = jwtProvider.verifyToken(token);
        String role = claims.get("role", String.class);

        if (Role.of(role) != Role.ADMIN) {
            throw new AuthorizationFailureException();
        }

        return true;
    }

    private Optional<Cookie> extractCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return Optional.of(cookie);
            }
        }

        return Optional.empty();
    }
}

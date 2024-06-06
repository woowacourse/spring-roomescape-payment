package roomescape.auth;

import static roomescape.exception.RoomescapeExceptionCode.UNAUTHORIZED;

import java.util.Arrays;
import java.util.Objects;

import jakarta.servlet.http.Cookie;

import org.springframework.stereotype.Component;

import roomescape.exception.RoomescapeException;

@Component
public class CookieProvider {

    private static final String TOKEN_KEY = "token";

    public Cookie createCookie(final String token) {
        final Cookie cookie = new Cookie(TOKEN_KEY, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

    public String extractToken(final Cookie[] cookies) {
        if (cookies == null) {
            throw new RoomescapeException(UNAUTHORIZED);
        }
        return Arrays.stream(cookies)
                .filter(cookie -> Objects.equals(cookie.getName(), TOKEN_KEY))
                .findFirst()
                .orElseThrow(() -> new RoomescapeException(UNAUTHORIZED))
                .getValue();
    }

    public Cookie expireCookie() {
        final Cookie cookie = new Cookie(TOKEN_KEY, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }
}

package roomescape.core.service;

import static roomescape.core.exception.ExceptionMessage.TOKEN_NOT_FOUND_EXCEPTION;

import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import roomescape.core.dto.auth.TokenResponse;

public class CookieService {
    protected static final String COOKIE_NAME = "token";

    public Cookie createCookie(final TokenResponse tokenResponse) {
        final Cookie cookie = new Cookie(COOKIE_NAME, tokenResponse.accessToken());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

    public Cookie createEmptyCookie() {
        final Cookie cookie = new Cookie(COOKIE_NAME, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }

    public String extractCookies(final Cookie[] cookies) {
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(COOKIE_NAME))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new IllegalArgumentException(TOKEN_NOT_FOUND_EXCEPTION.getMessage()));
    }
}

package roomescape.controller.login;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;
import roomescape.exception.login.InvalidTokenException;

import java.util.Arrays;

@Component
public class AuthCookieHandler {
    private static final String COOKIE_NAME = "token";

    public Cookie createCookie(String token) {
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

    public Cookie deleteCookie() {
        Cookie cookie = new Cookie(COOKIE_NAME, null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

    public String getToken(Cookie[] cookies) {
        if (cookies == null) {
            throw new InvalidTokenException();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(COOKIE_NAME))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(InvalidTokenException::new);
    }
}

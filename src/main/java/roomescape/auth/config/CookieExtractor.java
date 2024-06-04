package roomescape.auth.config;

import static roomescape.exception.type.RoomescapeExceptionType.REQUIRED_LOGIN;

import java.util.Arrays;
import java.util.Optional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import roomescape.exception.UnAuthorizedException;

public class CookieExtractor {
    private CookieExtractor() {
    }

    public static Cookie getTokenCookie(HttpServletRequest webRequest) {
        Optional<Cookie> accessTokenCookie = CookieExtractor.getCookie(webRequest, "token");
        if (accessTokenCookie.isEmpty()) {
            throw new UnAuthorizedException(REQUIRED_LOGIN);
        }
        return accessTokenCookie.get();
    }

    private static Optional<Cookie> getCookie(HttpServletRequest webRequest, String name) {
        Cookie[] cookies = webRequest.getCookies();
        if (cookies == null || cookies.length == 0) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst();
    }
}

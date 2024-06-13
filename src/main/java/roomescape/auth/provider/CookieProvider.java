package roomescape.auth.provider;

import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseCookie;
import roomescape.auth.domain.Token;
import roomescape.auth.exception.CookieException;
import roomescape.exception.RoomEscapeException;

import java.util.Arrays;

public class CookieProvider {

    public static final String SAME_SITE_OPTION = "Strict";
    private static final String TOKEN_NAME = "token";

    private CookieProvider() {
    }

    public static ResponseCookie setCookieFrom(Token token) {
        return ResponseCookie.from(TOKEN_NAME, token.getToken())
                .path("/")
                .sameSite(SAME_SITE_OPTION)
                .httpOnly(true)
                .maxAge((long)60 * 60 * 24)
                .build();
    }

    public static String getCookieValue(String name, Cookie[] cookies) {
        if (cookies == null) {
            throw new RoomEscapeException(CookieException.COOKIE_IS_NULL_EXCEPTION);
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(name))
                .map(Cookie::getValue)
                .findAny()
                .orElseThrow(() -> new RoomEscapeException(CookieException.COOKE_VALUE_IS_NOT_FOUND_EXCEPTION));
    }
}

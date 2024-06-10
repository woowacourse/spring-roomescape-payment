package roomescape.auth.provider;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import org.springframework.http.ResponseCookie;
import roomescape.auth.domain.Token;
import roomescape.auth.exception.CookieException;
import roomescape.exception.RoomEscapeException;

@Tag(name = "쿠키 제공자", description = "토큰을 사용해 쿠키를 만든다. 특정 이름의 쿠키를 찾아 해당 쿠키의 값을 반환한다.")
public class CookieProvider {

    private static final String TOKEN_NAME = "token";
    public static final String SAME_SITE_OPTION = "Strict";

    public static ResponseCookie setCookieFrom(Token token) {
        return ResponseCookie.from(TOKEN_NAME, token.getToken())
                .path("/")
                .sameSite(SAME_SITE_OPTION)
                .httpOnly(true)
                .maxAge(60 * 60 * 24)
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

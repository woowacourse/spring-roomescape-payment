package roomescape.auth.controller;

import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import roomescape.advice.exception.ExceptionTitle;
import roomescape.advice.exception.RoomEscapeException;

@Component
public class TokenCookieManager {
    private static final String ACCESS_TOKEN_KEY = "token";
    private static final int MIN = 60;
    private static final int ACCESS_TOKEN_MAX_AGE = 30 * MIN;

    public String getToken(Cookie[] cookies) {
        if (cookies == null) {
            throw new RoomEscapeException("인증이 되지 않은 유저입니다.", ExceptionTitle.AUTHENTICATION_FAILED);
        }
        return Arrays.stream(cookies)
                .filter(cookie -> ACCESS_TOKEN_KEY.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findAny()
                .orElseThrow(() ->
                        new RoomEscapeException("인증이 되지 않은 유저입니다.", ExceptionTitle.AUTHENTICATION_FAILED));
    }

    public ResponseCookie createResponseCookie(String token) {
        return ResponseCookie.from(ACCESS_TOKEN_KEY, token)
                .httpOnly(true)
                .path("/")
                .maxAge(ACCESS_TOKEN_MAX_AGE)
                .build();
    }
}

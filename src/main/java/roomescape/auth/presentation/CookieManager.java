package roomescape.auth.presentation;

import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import roomescape.auth.config.JwtProperties;

@Component
@RequiredArgsConstructor
public final class CookieManager {

    private static final String LOGIN_COOKIE_NAME = "token";

    private final JwtProperties jwtProperties;

    public ResponseCookie generateLoginCookie(final String token) {
        return ResponseCookie.from(LOGIN_COOKIE_NAME, token)
                .httpOnly(true)
                .path("/")
                .maxAge(jwtProperties.getExpireLength())
                .build();
    }

    public ResponseCookie generateLogoutCookie() {
        return ResponseCookie.from(LOGIN_COOKIE_NAME, "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
    }

    public String extractLoginToken(final Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> LOGIN_COOKIE_NAME.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}

package roomescape.web.auth;

import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieHandler {

    private static final String COOKIE_NAME = "token";
    private static final String COOKIE_PATH = "/";
    private static final int ONE_MINUTE = 60000;

    private final int expireLength;

    private CookieHandler(@Value("${security.jwt.token.expire-length}") int expireLength) {
        this.expireLength = expireLength;
    }

    public Cookie createCookieByToken(String token) {
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setPath(COOKIE_PATH);
        cookie.setMaxAge(expireLength * ONE_MINUTE);
        return cookie;
    }

    public String extractTokenFromCookies(Cookie[] cookies) {
        Optional<String> token = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(COOKIE_NAME))
                .map(Cookie::getValue)
                .findFirst();

        return token.orElseThrow(() -> new IllegalArgumentException("토큰이 존재하지 않습니다."));
    }
}

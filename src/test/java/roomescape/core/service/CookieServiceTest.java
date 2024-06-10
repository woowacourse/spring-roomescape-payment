package roomescape.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;

class CookieServiceTest {
    private final CookieService cookieService = new CookieService();

    @Test
    void createEmptyCookie() {
        final Cookie cookie = cookieService.createEmptyCookie();

        assertAll(
                () -> assertThat(cookie.getPath()).isEqualTo("/"),
                () -> assertThat(cookie.getMaxAge()).isZero()
        );
    }

    @Test
    void extractCookies() {
        final Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie("token", "test");

        final String extractedToken = cookieService.extractCookies(cookies);

        assertThat(extractedToken).isEqualTo("test");
    }
}

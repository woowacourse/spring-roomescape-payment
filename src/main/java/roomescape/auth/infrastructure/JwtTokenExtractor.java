package roomescape.auth.infrastructure;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import roomescape.auth.domain.AuthTokenExtractor;
import roomescape.exception.auth.AuthTokenNotFoundException;

@Slf4j
@Component
public class JwtTokenExtractor implements AuthTokenExtractor<String> {

    public static final String AUTH_TOKEN_NAME = "token";

    @Override
    public String extract(final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            log.warn("토큰 추출 실패: 요청에 쿠키가 없습니다.");
            throw new AuthTokenNotFoundException("쿠키가 존재하지 않습니다.");
        }

        return Arrays.stream(cookies)
                .filter(cookie -> AUTH_TOKEN_NAME.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> {
                    log.warn("토큰 추출 실패: 쿠키에 '{}' 이 존재하지 않습니다.", AUTH_TOKEN_NAME);
                    return new AuthTokenNotFoundException("쿠키에 " + AUTH_TOKEN_NAME + "이 존재하지 않습니다.");
                });
    }
}

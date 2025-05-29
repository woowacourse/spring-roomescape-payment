package roomescape.presentation.support;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import roomescape.infrastructure.error.exception.JwtExtractException;
import roomescape.infrastructure.security.AccessToken;

import java.util.Arrays;

@Component
public class JwtTokenExtractor {

    private final static String JWT_TOKEN_COOKIE_KEY = "token";

    public AccessToken extract(final HttpServletRequest request) {
        return AccessToken.of(getTokenCookie(request).getValue());
    }

    private Cookie getTokenCookie(final HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new JwtExtractException("인증 쿠키값이 존재하지 않습니다.");
        }
        return Arrays.stream(request.getCookies())
                .filter(each -> each.getName().equals(JWT_TOKEN_COOKIE_KEY))
                .findFirst()
                .orElseThrow(() -> new JwtExtractException("인증 쿠키값이 존재하지 않습니다."));
    }
}

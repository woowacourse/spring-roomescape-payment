package roomescape.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import roomescape.exception.custom.UnauthorizedException;

@Slf4j
public class JwtExtractor {

    private JwtExtractor() {
    }

    public static String extractFromRequest(HttpServletRequest request) {
        log.info("HTTP 요청에서 JWT 토큰 추출 시작 - URI: {}", request.getRequestURI());

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            log.error("쿠키가 비어있음 - URI: {}", request.getRequestURI());
            throw new UnauthorizedException("쿠키가 비어있습니다.");
        }

        String token = extractTokenFromCookie(cookies);
        log.info("HTTP 요청에서 JWT 토큰 추출 완료 - URI: {}", request.getRequestURI());
        return token;
    }

    private static String extractTokenFromCookie(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                log.debug("쿠키에서 토큰 발견 - cookieName: {}", cookie.getName());
                return cookie.getValue();
            }
        }
        log.error("쿠키에 토큰이 없음 - cookieCount: {}", cookies.length);
        throw new UnauthorizedException("쿠키에 토큰이 없습니다.");
    }
}

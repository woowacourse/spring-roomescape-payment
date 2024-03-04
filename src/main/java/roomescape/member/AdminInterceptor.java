package roomescape.member;

import auth.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminInterceptor implements HandlerInterceptor {
    private JwtUtils jwtUtils;

    public AdminInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();

        String token = extractTokenFromCookie(cookies);

        String role = jwtUtils.extractClaim(token, "role");

        if ("ADMIN".equals(role)) {
            response.setStatus(401);
            return false;
        }

        return true;
    }

    private String extractTokenFromCookie(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                return cookie.getValue();
            }
        }

        throw new RuntimeException("로그인이 필요합니다.");
    }
}
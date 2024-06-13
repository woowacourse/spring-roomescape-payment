package roomescape.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.dto.response.member.MemberResponse;
import roomescape.exception.AuthorizedException;
import roomescape.service.AuthService;

@Component
public class CheckAdminInterceptor implements HandlerInterceptor {
    private final AuthService authService;

    public CheckAdminInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Cookie[] cookies = request.getCookies();
        MemberResponse memberResponse = authService.findMemberByCookies(cookies);
        if (memberResponse.role().isAdmin()) {
            return true;
        }
        throw new AuthorizedException(String.format("관리자 권한이 없는 사용자입니다. 사용자 id:%d", memberResponse.id()));
    }
}

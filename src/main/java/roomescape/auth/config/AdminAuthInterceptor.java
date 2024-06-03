package roomescape.auth.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import roomescape.auth.service.LoginService;
import roomescape.exception.type.RoomescapeExceptionType;
import roomescape.exception.RoomescapeException;
import roomescape.member.domain.LoginMember;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final LoginService loginService;

    public AdminAuthInterceptor(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String accessToken = CookieExtractor.getTokenCookie(request).getValue();
        LoginMember loginMember = loginService.checkLogin(accessToken);
        if (!loginMember.isAdmin()) {
            throw new RoomescapeException(RoomescapeExceptionType.PERMISSION_DENIED, loginMember.getRole());
        }
        return true;
    }
}

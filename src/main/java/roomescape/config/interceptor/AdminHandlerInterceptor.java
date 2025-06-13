package roomescape.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.dto.request.LoginMemberRequest;
import roomescape.exception.custom.ForbiddenException;
import roomescape.global.Role;
import roomescape.jwt.JwtExtractor;
import roomescape.service.AuthService;

@Component
@Slf4j
public class AdminHandlerInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    public AdminHandlerInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("관리자 권한 확인 시작 - URI: {}", request.getRequestURI());

        String token = JwtExtractor.extractFromRequest(request);
        LoginMemberRequest loginMemberRequest = authService.getLoginMemberByToken(token);

        if (loginMemberRequest.role() == Role.ADMIN) {
            log.info("관리자 권한 확인 완료 - memberId: {}, role: {}",
                    loginMemberRequest.id(), loginMemberRequest.role());
            return true;
        }

        log.error("관리자 권한 없음 - memberId: {}, role: {}, URI: {}",
                loginMemberRequest.id(), loginMemberRequest.role(), request.getRequestURI());
        throw new ForbiddenException("어드민만 접근 가능한 페이지입니다.");
    }
}

package roomescape.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.annotation.CheckRole;
import roomescape.dto.request.LoginMemberRequest;
import roomescape.exception.custom.ForbiddenException;
import roomescape.global.Role;
import roomescape.jwt.JwtExtractor;
import roomescape.service.AuthService;

@Component
@Slf4j
public class CheckRoleHandlerInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    public CheckRoleHandlerInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        CheckRole checkRole = handlerMethod.getMethodAnnotation(CheckRole.class);
        if (checkRole == null) {
            return true;
        }

        log.info("역할 권한 확인 시작 - URI: {}, requiredRoles: {}",
                request.getRequestURI(), Arrays.toString(checkRole.value()));

        String token = JwtExtractor.extractFromRequest(request);
        LoginMemberRequest loginMemberRequest = authService.getLoginMemberByToken(token);

        if (loginMemberRequest == null) {
            log.error("로그인 정보 없음 - URI: {}", request.getRequestURI());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "로그인이 필요한 서비스입니다.");
            return false;
        }

        Role[] roles = checkRole.value();
        if (Arrays.stream(roles).noneMatch(role -> role == loginMemberRequest.role())) {
            log.error("권한 부족 - memberId: {}, userRole: {}, requiredRoles: {}, URI: {}",
                    loginMemberRequest.id(), loginMemberRequest.role(),
                    Arrays.toString(roles), request.getRequestURI());
            throw new ForbiddenException("접근 불가한 페이지입니다.");
        }

        log.info("역할 권한 확인 완료 - memberId: {}, role: {}, URI: {}",
                loginMemberRequest.id(), loginMemberRequest.role(), request.getRequestURI());
        return true;
    }
}

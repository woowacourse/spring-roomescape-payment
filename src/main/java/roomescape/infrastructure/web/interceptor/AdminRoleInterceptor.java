package roomescape.infrastructure.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.common.token.JwtTokenManager;
import roomescape.common.token.TokenExtractor;
import roomescape.exception.auth.ForbiddenException;
import roomescape.exception.auth.JwtExtractException;
import roomescape.exception.auth.UnauthorizedException;

@Component
@RequiredArgsConstructor
public class AdminRoleInterceptor implements HandlerInterceptor {

    public static final String ADMIN_STRING = "ADMIN";

    private final JwtTokenManager jwtTokenManager;

    private static void validateRoleIsAdmin(final String role) {
        if (!role.equals(ADMIN_STRING)) {
            throw new ForbiddenException("관리자가 아닙니다.");
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = TokenExtractor.extract(request);

        try {
            String role = jwtTokenManager.getRole(token);
            validateRoleIsAdmin(role);

            return true;
        } catch (final JwtExtractException exception) {
            throw new UnauthorizedException(exception.getMessage());
        }
    }
}

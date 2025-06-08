package roomescape.auth.ui;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.auth.domain.AuthRole;
import roomescape.auth.domain.AuthTokenExtractor;
import roomescape.auth.domain.AuthTokenProvider;
import roomescape.auth.domain.RequiresRole;
import roomescape.exception.auth.AuthenticationException;
import roomescape.exception.auth.AuthorizationException;

@Slf4j
@RequiredArgsConstructor
public class AuthRoleCheckInterceptor implements HandlerInterceptor {

    private final AuthTokenExtractor<String> authTokenExtractor;
    private final AuthTokenProvider authTokenProvider;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, Object handler)
            throws AuthenticationException {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequiresRole requiresRole = handlerMethod.getMethodAnnotation(RequiresRole.class);
        if (requiresRole == null) {
            requiresRole = handlerMethod.getBeanType().getAnnotation(RequiresRole.class);
        }

        if (requiresRole == null) {
            return true;
        }

        final String accessToken = authTokenExtractor.extract(request);
        log.debug("Access token 추출 완료 (내용 생략)");

        if (!authTokenProvider.isValidToken(accessToken)) {
            log.warn("유효하지 않은 토큰 요청. URI: {}", request.getRequestURI());
            throw new AuthenticationException("유효하지 않은 토큰입니다.");
        }

        final AuthRole role = authTokenProvider.getRole(accessToken);
        final boolean authorized = Arrays.stream(requiresRole.authRoles())
                .anyMatch(authRole -> authRole == role);

        if (!authorized) {
            log.warn("권한 부족: 요청 URI={}, 필요 권한={}, 사용자 권한={}",
                    request.getRequestURI(),
                    Arrays.toString(requiresRole.authRoles()),
                    role);
            throw new AuthorizationException("권한이 없습니다.");
        }

        log.info("권한 인증 성공: URI={}, 사용자 권한={}", request.getRequestURI(), role);
        return true;
    }
}

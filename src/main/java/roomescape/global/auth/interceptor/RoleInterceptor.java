package roomescape.global.auth.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.global.auth.annotation.RequireRole;
import roomescape.global.auth.exception.ForbiddenException;
import roomescape.global.auth.exception.UnAuthorizedException;
import roomescape.global.auth.infrastructure.AuthorizationExtractor;
import roomescape.global.auth.infrastructure.JwtProvider;
import roomescape.member.domain.MemberRole;

@Slf4j
public class RoleInterceptor implements HandlerInterceptor {

    private static final String ADMIN = "/admin";

    private final AuthorizationExtractor authorizationExtractor;
    private final JwtProvider jwtProvider;

    public RoleInterceptor(final AuthorizationExtractor authorizationExtractor, final JwtProvider jwtProvider) {
        this.authorizationExtractor = authorizationExtractor;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
            throws Exception {

        String path = request.getRequestURI();
        if (path.startsWith(ADMIN)) {
            return validateToken(request, MemberRole.ADMIN);
        }

        if (!(handler instanceof final HandlerMethod handlerMethod)) {
            return true;
        }

        RequireRole classAnnotation = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        RequireRole methodAnnotation = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (classAnnotation != null) {
            return validateToken(request, classAnnotation.value());
        }
        if (methodAnnotation != null) {
            return validateToken(request, methodAnnotation.value());
        }
        return true;
    }

    private boolean validateToken(final HttpServletRequest request, final MemberRole memberRole) {
        String token = authorizationExtractor.extract(request);
        if (token == null) {
            log.info("토큰 부재로 인한 로그인 확인 실패 url = {}", request.getRequestURI());
            throw new UnAuthorizedException("토큰이 존재하지 않습니다.");
        }
        if ((memberRole == MemberRole.ADMIN) && (jwtProvider.getRole(token) != MemberRole.ADMIN)) {
            log.info("권한 부족으로 접근 불가능 url = {}", request.getRequestURI());
            throw new ForbiddenException("접근할 수 없습니다.");
        }
        return true;
    }
}

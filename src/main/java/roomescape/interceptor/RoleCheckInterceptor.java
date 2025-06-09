package roomescape.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.annotation.Authority;
import roomescape.exception.ForbiddenException;
import roomescape.exception.NotFoundException;
import roomescape.exception.UnauthorizedException;
import roomescape.mvc.auth.dto.AccessTokenContent;
import roomescape.mvc.member.domain.Role;
import roomescape.utility.CookieUtility;
import roomescape.utility.JwtTokenProvider;

public class RoleCheckInterceptor implements HandlerInterceptor {

    private final CookieUtility cookieUtility;
    private final JwtTokenProvider jwtTokenProvider;

    public RoleCheckInterceptor(CookieUtility cookieUtility, JwtTokenProvider jwtTokenProvider) {
        this.cookieUtility = cookieUtility;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (isStaticRequest(handler)) {
            return true;
        }
        if (isNotAuthorityRequest(handler)) {
            return true;
        }

        Authority authorityAnnotation = getAuthorityAnnotation(handler);
        AccessTokenContent token = getAccessTokenInCookie(request);

        if (authorityAnnotation.value() == Role.ADMIN) {
            return canEnterAdminRequest(token);
        }
        if (authorityAnnotation.value() == Role.GENERAL) {
            return canEnterGeneralRequest(token);
        }

        return true;
    }

    private boolean isStaticRequest(Object handler) {
        return !(handler instanceof HandlerMethod);
    }

    private boolean isNotAuthorityRequest(Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Authority authority = handlerMethod.getMethodAnnotation(Authority.class);
        return authority == null;
    }

    private boolean canEnterAdminRequest(AccessTokenContent token) {
        if (token.role() == Role.ADMIN) {
            return true;
        }
        throw new ForbiddenException("권한이 존재하지 않습니다.");
    }

    private boolean canEnterGeneralRequest(AccessTokenContent token) {
        if (token.role() == Role.GENERAL || token.role() == Role.ADMIN) {
            return true;
        }
        throw new ForbiddenException("권한이 존재하지 않습니다.");
    }

    private Authority getAuthorityAnnotation(Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        return handlerMethod.getMethodAnnotation(Authority.class);
    }

    private AccessTokenContent getAccessTokenInCookie(HttpServletRequest request) {
        Cookie accessTokenCookie = getAcceccTokenCookie(request);
        return jwtTokenProvider.parseAccessToken(accessTokenCookie.getValue());
    }

    private Cookie getAcceccTokenCookie(HttpServletRequest request) {
        try {
            return cookieUtility.getCookie(request, "access");
        } catch (NotFoundException exception) {
            throw new UnauthorizedException("인증 정보가 존재하지 않습니다.");
        }
    }
}

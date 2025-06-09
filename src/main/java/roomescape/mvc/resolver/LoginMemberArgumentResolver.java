package roomescape.mvc.resolver;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.domain.annotation.RequiredAccessToken;
import roomescape.domain.auth.dto.AccessTokenContent;
import roomescape.exception.NotFoundException;
import roomescape.exception.UnauthorizedException;
import roomescape.utility.CookieUtility;
import roomescape.utility.JwtTokenProvider;

public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final CookieUtility cookieUtility;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginMemberArgumentResolver(CookieUtility cookieUtility, JwtTokenProvider jwtTokenProvider) {
        this.cookieUtility = cookieUtility;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(RequiredAccessToken.class) &&
                methodParameter.getParameterType().isAssignableFrom(AccessTokenContent.class);
    }

    @Override
    public AccessTokenContent resolveArgument(
            MethodParameter methodParameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest nativeWebRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
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

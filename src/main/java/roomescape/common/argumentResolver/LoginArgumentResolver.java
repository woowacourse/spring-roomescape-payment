package roomescape.common.argumentResolver;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.common.util.JwtTokenContainer;
import roomescape.common.util.TokenCookieManager;
import roomescape.member.dto.request.LoginMember;

public class LoginArgumentResolver implements HandlerMethodArgumentResolver {

    private final TokenCookieManager tokenCookieManager;
    private final JwtTokenContainer tokenContainer;


    public LoginArgumentResolver(final TokenCookieManager tokenCookieManager, final JwtTokenContainer tokenContainer) {
        this.tokenCookieManager = tokenCookieManager;
        this.tokenContainer = tokenContainer;
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        boolean hasAnnotation = parameter.hasParameterAnnotation(Login.class);
        boolean hasLoginMemberType = LoginMember.class.isAssignableFrom(parameter.getParameterType());

        return hasAnnotation && hasLoginMemberType;
    }

    @Override
    public LoginMember resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
                                       final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String token = tokenCookieManager.extractTokenFromCookie(request);
        Long memberId = tokenContainer.getMemberId(token);
        return new LoginMember(memberId);
    }
}

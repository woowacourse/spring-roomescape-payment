package roomescape.config.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.controller.login.AuthCookieHandler;
import roomescape.domain.member.Member;
import roomescape.service.login.LoginService;

public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {
    private final LoginService loginService;
    private final AuthCookieHandler authCookieHandler;

    public LoginMemberArgumentResolver(LoginService loginService, AuthCookieHandler authCookieHandler) {
        this.loginService = loginService;
        this.authCookieHandler = authCookieHandler;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class);
    }

    @Override
    public Member resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String token = authCookieHandler.getToken(request.getCookies());
        return loginService.findMemberByToken(token);
    }
}

package roomescape.config.resolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.dto.request.LoginMemberRequest;
import roomescape.jwt.JwtExtractor;
import roomescape.service.AuthService;

@Component
@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthService authService;

    public LoginMemberArgumentResolver(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(LoginMemberRequest.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        log.info("LoginMemberRequest 인자 해석 시작 - parameterType: {}", parameter.getParameterType());

        String token = JwtExtractor.extractFromRequest((HttpServletRequest) webRequest.getNativeRequest());
        LoginMemberRequest loginMemberRequest = authService.getLoginMemberByToken(token);

        log.info("LoginMemberRequest 인자 해석 완료 - memberId: {}, name: {}",
                loginMemberRequest.id(), loginMemberRequest.name());
        return loginMemberRequest;
    }
}

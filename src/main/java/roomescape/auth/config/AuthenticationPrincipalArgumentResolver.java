package roomescape.auth.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.auth.application.AuthService;
import roomescape.auth.application.LoginMember;
import roomescape.auth.exception.AuthenticationException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationPrincipalArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthService authService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAuthenticationAnnotation = parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
        boolean isAuthMemberType = LoginMember.class.isAssignableFrom(parameter.getParameterType());
        return hasAuthenticationAnnotation && isAuthMemberType;
    }

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        try {
            return authService.extractMemberByRequest(request);
        } catch (Exception e) {
            log.info("[로그인 정보 추출 실패] message: {}", e.getMessage());
            throw new AuthenticationException("유효한 로그인 정보가 없습니다. 다시 로그인해 주세요.");
        }
    }
}

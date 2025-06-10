package roomescape.infrastructure.web.argumentresolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.annotation.LoginMember;
import roomescape.common.token.JwtTokenManager;
import roomescape.common.token.TokenExtractor;
import roomescape.dto.auth.info.LoginMemberInfo;
import roomescape.exception.auth.ForbiddenException;

@Component
@RequiredArgsConstructor
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String MEMBER_STRING = "MEMBER";

    private final JwtTokenManager jwtTokenManager;

    private static void validateRoleIsMember(String role) {
        if (!role.equals(MEMBER_STRING)) {
            throw new ForbiddenException("멤버가 아닙니다.");
        }
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String token = TokenExtractor.extract(request);

        String role = jwtTokenManager.getRole(token);
        validateRoleIsMember(role);

        return new LoginMemberInfo(jwtTokenManager.getId(token));
    }
}

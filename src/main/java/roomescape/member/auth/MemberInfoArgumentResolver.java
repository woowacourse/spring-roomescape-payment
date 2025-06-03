package roomescape.member.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.common.exception.AuthorizationException;
import roomescape.member.auth.jwt.JwtTokenExtractor;

@RequiredArgsConstructor
public class MemberInfoArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenExtractor jwtTokenExtractor;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class);
    }

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) {
        final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        try {
            final String token = jwtTokenExtractor.extractTokenFromCookie(request.getCookies());
            return jwtTokenExtractor.extractMemberInfoFromToken(token);
        } catch (Exception e) {
            validateLoginMemberRequired(parameter.getParameterAnnotation(LoginMember.class));
            return null;
        }
    }

    private void validateLoginMemberRequired(final LoginMember loginMember) {
        if (loginMember.required()) {
            throw new AuthorizationException();
        }
    }
}

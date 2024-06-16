package roomescape.web.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.application.dto.request.member.MemberInfo;
import roomescape.application.security.JwtProvider;
import roomescape.exception.member.AuthenticationFailureException;

@Component
@RequiredArgsConstructor
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtProvider jwtProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(MemberInfo.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String token = extractCookie(request.getCookies(), "token");
        Claims claims = jwtProvider.verifyToken(token);

        long memberId = Long.parseLong(claims.getSubject());
        String name = claims.get("name", String.class);

        return new MemberInfo(memberId, name);
    }

    private String extractCookie(Cookie[] cookies, String targetCookie) {
        if (cookies == null) {
            throw new AuthenticationFailureException();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(targetCookie))
                .findAny()
                .map(Cookie::getValue)
                .orElseThrow(AuthenticationFailureException::new);
    }
}

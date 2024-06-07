package roomescape.web.support;

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
import roomescape.application.security.JwtProvider;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.exception.AuthenticationException;

@Component
@RequiredArgsConstructor
public class MemberArgumentResolver implements HandlerMethodArgumentResolver {
    private static final String TARGET_COOKIE_NAME = "token";

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Member.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String token = extractCookie(request.getCookies(), TARGET_COOKIE_NAME);
        return getMember(token);
    }

    private String extractCookie(Cookie[] cookies, String targetCookie) {
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(targetCookie))
                .findAny()
                .map(Cookie::getValue)
                .orElseThrow(AuthenticationException::new);
    }

    private Member getMember(String token) {
        Long memberId = jwtProvider.extractId(token);
        return memberRepository.findById(memberId)
                .orElseThrow(AuthenticationException::new);
    }
}

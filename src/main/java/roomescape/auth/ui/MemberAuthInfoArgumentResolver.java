package roomescape.auth.ui;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.auth.domain.AuthRole;
import roomescape.auth.domain.AuthTokenExtractor;
import roomescape.auth.domain.AuthTokenProvider;
import roomescape.auth.domain.MemberAuthInfo;
import roomescape.exception.auth.AuthenticationException;

@Slf4j
@RequiredArgsConstructor
public class MemberAuthInfoArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthTokenExtractor<String> authTokenExtractor;
    private final AuthTokenProvider authTokenProvider;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().equals(MemberAuthInfo.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) {

        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        final String token = authTokenExtractor.extract(request);
        log.info("토큰 추출 완료: {}", token);

        if (!authTokenProvider.isValidToken(token)) {
            log.warn("유효하지 않은 토큰 감지: {}", token);
            throw new AuthenticationException("유효하지 않은 토큰입니다.");
        }

        final Long id = Long.parseLong(authTokenProvider.getPrincipal(token));
        final AuthRole role = authTokenProvider.getRole(token);
        log.info("토큰 유효성 검사 통과 - memberId: {}, role: {}", id, role);

        return new MemberAuthInfo(id, role);
    }
}

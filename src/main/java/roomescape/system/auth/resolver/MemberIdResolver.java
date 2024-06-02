package roomescape.system.auth.resolver;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.system.auth.annotation.MemberId;
import roomescape.system.auth.jwt.JwtHandler;
import roomescape.system.exception.ErrorType;
import roomescape.system.exception.RoomEscapeException;

@Component
public class MemberIdResolver implements HandlerMethodArgumentResolver {
    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";

    private final JwtHandler jwtHandler;

    public MemberIdResolver(final JwtHandler jwtHandler) {
        this.jwtHandler = jwtHandler;
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(MemberId.class);
    }

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) throws Exception {
        final Cookie[] cookies = webRequest.getNativeRequest(HttpServletRequest.class).getCookies();
        if (cookies == null) {
            throw new RoomEscapeException(ErrorType.NOT_EXIST_COOKIE, HttpStatus.UNAUTHORIZED);
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(ACCESS_TOKEN_COOKIE_NAME))
                .findAny()
                .map(cookie -> jwtHandler.getMemberIdFromToken(cookie.getValue()))
                .orElseThrow(() -> new RoomEscapeException(ErrorType.INVALID_TOKEN, HttpStatus.UNAUTHORIZED));
    }
}

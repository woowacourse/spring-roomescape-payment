package roomescape.system.auth.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.member.domain.Member;
import roomescape.member.service.MemberService;
import roomescape.system.auth.annotation.LoginRequired;
import roomescape.system.auth.jwt.JwtHandler;
import roomescape.system.exception.ErrorType;
import roomescape.system.exception.RoomEscapeException;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private final MemberService memberService;
    private final JwtHandler jwtHandler;

    public LoginInterceptor(MemberService memberService, JwtHandler jwtHandler) {
        this.memberService = memberService;
        this.jwtHandler = jwtHandler;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    )
            throws Exception {
        if (isHandlerIrrelevantWithLoginRequired(handler)) {
            return true;
        }

        Member member;
        try {
            Cookie token = getToken(request);
            Long memberId = jwtHandler.getMemberIdFromToken(token.getValue());
            member = memberService.findMemberById(memberId);
            return member != null;
        } catch (RoomEscapeException e) {
            throw new RoomEscapeException(ErrorType.LOGIN_REQUIRED, HttpStatus.FORBIDDEN);
        }
    }

    private Cookie getToken(HttpServletRequest request) {
        validateCookieHeader(request);

        Cookie[] cookies = request.getCookies();
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(ACCESS_TOKEN_COOKIE_NAME))
                .findAny()
                .orElseThrow(() -> new RoomEscapeException(ErrorType.INVALID_TOKEN, HttpStatus.UNAUTHORIZED));
    }

    private void validateCookieHeader(HttpServletRequest request) {
        String cookieHeader = request.getHeader("Cookie");
        if (cookieHeader == null) {
            throw new RoomEscapeException(ErrorType.NOT_EXIST_COOKIE, HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean isHandlerIrrelevantWithLoginRequired(Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        LoginRequired loginRequiredAnnotation = handlerMethod.getMethodAnnotation(LoginRequired.class);
        return loginRequiredAnnotation == null;
    }
}
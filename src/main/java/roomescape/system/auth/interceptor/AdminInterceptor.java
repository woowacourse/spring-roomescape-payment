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
import roomescape.system.auth.annotation.Admin;
import roomescape.system.auth.jwt.JwtHandler;
import roomescape.system.exception.ErrorType;
import roomescape.system.exception.RoomEscapeException;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private final MemberService memberService;
    private final JwtHandler jwtHandler;

    public AdminInterceptor(MemberService memberService, JwtHandler jwtHandler) {
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
        if (isHandlerIrrelevantWithAdmin(handler)) {
            return true;
        }

        Cookie token = getToken(request);
        Long memberId = jwtHandler.getMemberIdFromToken(token.getValue());
        Member member = memberService.findMemberById(memberId);

        if (member.isAdmin()) {
            return true;
        }

        throw new RoomEscapeException(ErrorType.PERMISSION_DOES_NOT_EXIST,
                String.format("[memberId: %d, Role: %s]", member.getId(), member.getRole()), HttpStatus.FORBIDDEN);
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

    private boolean isHandlerIrrelevantWithAdmin(Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        Admin adminAnnotation = handlerMethod.getMethodAnnotation(Admin.class);
        return adminAnnotation == null;
    }
}

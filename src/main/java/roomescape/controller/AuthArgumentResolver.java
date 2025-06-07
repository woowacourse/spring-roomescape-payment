package roomescape.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.controller.annotation.AdminOnly;
import roomescape.controller.annotation.CurrentMember;
import roomescape.controller.util.CookieHandler;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.dto.auth.LoginInfo;
import roomescape.exception.AccessDeniedException;
import roomescape.exception.common.UnauthorizedException;
import roomescape.exception.common.NotFoundException;
import roomescape.service.query.MemberQueryService;
import roomescape.util.JwtTokenProvider;

@Component
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberQueryService memberQueryService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieHandler cookieHandler;

    public AuthArgumentResolver(MemberQueryService memberQueryService, JwtTokenProvider jwtTokenProvider,
                                CookieHandler cookieHandler) {
        this.memberQueryService = memberQueryService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.cookieHandler = cookieHandler;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return (parameter.hasParameterAnnotation(CurrentMember.class) || parameter.hasParameterAnnotation(AdminOnly.class))
                && parameter.getParameterType().equals(LoginInfo.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest nativeRequest = (HttpServletRequest) webRequest.getNativeRequest();
        Cookie[] cookies = nativeRequest.getCookies();
        String token = cookieHandler.extractCookie(cookies, "token");
        Long id = jwtTokenProvider.extractId(token);
        Member loginMember = findExistingMemberById(id);

        if (parameter.hasParameterAnnotation(AdminOnly.class)) {
            if (!loginMember.getRole().isAdmin()) {
                throw new AccessDeniedException(loginMember.getRole(), Role.ADMIN, loginMember.getId());
            }
        }
        return new LoginInfo(loginMember);
    }

    private Member findExistingMemberById(Long id) {
        try {
            return memberQueryService.findMemberById(id);
        } catch (NotFoundException e) {
            throw new UnauthorizedException("유저를 찾을 수 없습니다.", id);
        }
    }
}

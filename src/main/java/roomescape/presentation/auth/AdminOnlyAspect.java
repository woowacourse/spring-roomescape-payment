package roomescape.presentation.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import roomescape.domain.auth.AuthenticationTokenHandler;
import roomescape.exception.AuthorizationException;

@Aspect
@Component
@RequiredArgsConstructor
public class AdminOnlyAspect {

    private final HttpServletRequest request;
    private final AuthenticationTokenHandler tokenHandler;

    @Before("@annotation(adminOnly)")
    public void checkAdminAccess(final AdminOnly adminOnly) {
        var tokenCookie = AuthenticationTokenCookie.fromRequest(request);

        if (tokenCookie.hasToken() && tokenHandler.isValidToken(tokenCookie.token())) {
            var authInfo = tokenHandler.extractAuthenticationInfo(tokenCookie.token());
            if (authInfo.isAdmin()) {
                return;
            }
        }
        throw new AuthorizationException("관리자만 수행할 수 있는 작업입니다.");
    }
}

package roomescape.auth.sign.application;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.auth.sign.application.dto.SignInResult;
import roomescape.auth.sign.application.usecase.SignInUseCase;
import roomescape.auth.sign.application.usecase.SignUpUseCase;
import roomescape.auth.sign.ui.dto.SignInWebRequest;
import roomescape.auth.sign.ui.dto.SignUpWebRequest;
import roomescape.auth.sign.ui.dto.UserSessionResponse;
import roomescape.common.cookie.manager.CookieManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignFacadeImpl implements SignFacade {

    private final SignInUseCase signInUseCase;
    private final SignUpUseCase signUpUseCase;
    private final CookieManager cookieManager;

    @Override
    public void signIn(final SignInWebRequest signInWebRequest,
                       final HttpServletResponse httpServletResponse) {
        log.info("[SIGN] 로그인 요청: {}", signInWebRequest.email());
        final SignInResult result = signInUseCase.execute(
                signInWebRequest.toServiceRequest());

        cookieManager.setCookie(httpServletResponse, result.cookie());
    }

    @Override
    public UserSessionResponse signUp(final SignUpWebRequest request) {
        log.info("[SIGN] 회원가입 요청: {}", request.email());
        return UserSessionResponse.from(
                signUpUseCase.execute(request.toServiceRequest()));
    }
}

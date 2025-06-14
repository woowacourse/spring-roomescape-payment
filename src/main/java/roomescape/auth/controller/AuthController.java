package roomescape.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginCheckResponse;
import roomescape.auth.dto.LoginRequest;
import roomescape.auth.infrastructure.util.CookieManager;
import roomescape.auth.service.AuthService;
import roomescape.exception.UnauthorizedException;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private static final String COOKIE_TOKEN = "token";

    private final AuthService authService;
    private final CookieManager cookieManager;

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public void login(
            @Parameter(description = "로그인 요청 정보") @RequestBody @Valid final LoginRequest request,
            @Parameter(description = "HTTP 응답 객체") final HttpServletResponse response
    ) {
        log.debug("로그인 시작");
        final String token = authService.createToken(request);
        log.debug("토큰 생성 완료");

        final ResponseCookie cookie = cookieManager.generateLoginCookie(token);
        log.debug("쿠키 생성 완료");

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        log.debug("로그인 성공");
    }

    @Operation(summary = "로그아웃", description = "로그아웃을 수행합니다.")
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Parameter(description = "HTTP 응답 객체") final HttpServletResponse response) {
        final ResponseCookie cookie = cookieManager.generateLogoutCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @Operation(summary = "로그인 상태 확인", description = "현재 로그인 상태를 확인합니다.")
    @GetMapping("/login/check")
    public LoginCheckResponse checkLogin(
            @Parameter(description = "토큰 쿠키") @CookieValue(name = COOKIE_TOKEN, required = false) final String token
    ) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("토큰 쿠키가 존재하지 않습니다.");
        }
        return authService.checkLogin(token);
    }
}

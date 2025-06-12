package roomescape.presentation.api.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.auth.LoginService;
import roomescape.application.auth.dto.LoginResult;
import roomescape.presentation.support.methodresolver.AuthInfo;
import roomescape.presentation.support.methodresolver.AuthPrincipal;

import java.time.Duration;

@Tag(name = "사용자 인증 API")
@RestController
public class AuthController {

    private static final String TOKEN_COOKIE_KEY = "token";

    private final LoginService loginService;
    private final Duration tokenCookieDuration;

    public AuthController(final LoginService loginService,
                          @Value("${security.jwt.token.expire-duration}") final Duration tokenCookieDuration) {
        this.loginService = loginService;
        this.tokenCookieDuration = tokenCookieDuration;
    }

    @Operation(
            summary = "로그인",
            description = "사용자가 로그인합니다. 요청 본문에 이메일과 비밀번호를 포함해야 합니다."
    )
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody final LoginRequest loginRequest) {
        final LoginResult loginResult = loginService.login(loginRequest.toLoginCommand());
        final ResponseCookie jwtCookie = createCookie(TOKEN_COOKIE_KEY, loginResult.token(), tokenCookieDuration);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .build();
    }

    @Operation(
            summary = "로그인 상태 확인",
            description = "사용자가 로그인 상태를 확인합니다. 인증된 사용자의 이름을 반환합니다."
    )
    @GetMapping("/login/check")
    public ResponseEntity<LoginCheckResponse> loginCheck(@AuthPrincipal final AuthInfo authInfo) {
        return ResponseEntity.ok().body(new LoginCheckResponse(authInfo.name()));
    }

    @Operation(
            summary = "로그아웃",
            description = "사용자가 로그아웃합니다. 쿠키를 삭제하여 인증을 무효화합니다."
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        final ResponseCookie jwtCookie = createCookie(TOKEN_COOKIE_KEY, "", Duration.ZERO);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .build();
    }

    private ResponseCookie createCookie(final String name, final String value, final Duration maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false) //https 적용전 임시
                .path("/")
                .sameSite("Strict")
                .maxAge(maxAge)
                .build();
    }
}

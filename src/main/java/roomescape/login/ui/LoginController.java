package roomescape.login.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.login.application.LoginService;
import roomescape.login.application.TokenCookieService;
import roomescape.login.application.dto.LoginCheckRequest;
import roomescape.login.application.dto.LoginCheckResponse;
import roomescape.login.application.dto.LoginRequest;
import roomescape.login.application.dto.SignupRequest;
import roomescape.login.application.dto.Token;

@Tag(name = "인증", description = "인증 관련 API")
@RestController
public class LoginController {

    private final LoginService loginService;
    private final TokenCookieService tokenCookieService;

    @Value("${security.jwt.token.access.expire-length}")
    private long expiration;

    public LoginController(final LoginService loginService, final TokenCookieService tokenCookieService) {
        this.loginService = loginService;
        this.tokenCookieService = tokenCookieService;
    }

    @Operation(summary = "로그인 API", description = "로그인 요청을 처리합니다. 성공 시 JWT 토큰을 쿠키에 저장합니다.")
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody final LoginRequest request) {
        final Token token = loginService.login(request);
        final String cookie = tokenCookieService.createTokenCookie(token.accessToken(), expiration);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie)
                .build();
    }

    @Operation(summary = "로그인 상태 확인 API", description = "로그인 상태를 확인합니다. 성공 시 로그인 정보를 반환합니다.")
    @GetMapping("/login/check")
    public ResponseEntity<LoginCheckResponse> checkLogin(final LoginCheckRequest request) {
        final LoginCheckResponse loginCheckResponse = loginService.checkLogin(request);
        return ResponseEntity.ok(loginCheckResponse);
    }

    @Operation(summary = "로그아웃 API", description = "로그아웃 요청을 처리합니다. 성공 시 JWT 토큰을 삭제합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        final String cookie = tokenCookieService.createTokenCookie("", 0);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie)
                .build();
    }

    @Operation(summary = "회원 가입 API", description = "회원 가입 요청을 처리합니다. 성공 시 로그인 정보를 반환합니다.")
    @PostMapping("/signup")
    public ResponseEntity<LoginCheckResponse> signup(@Valid @RequestBody final SignupRequest request) {
        final LoginCheckResponse loginCheckResponse = loginService.signup(request);
        return ResponseEntity.ok(loginCheckResponse);
    }
}

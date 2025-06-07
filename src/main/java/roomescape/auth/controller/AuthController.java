package roomescape.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.service.AuthService;
import roomescape.auth.service.dto.LoginMember;
import roomescape.auth.service.dto.request.LoginRequest;
import roomescape.auth.service.dto.response.CheckLoginResponse;
import roomescape.auth.service.dto.response.LoginResponse;
import roomescape.common.AuthTokenCookieProvider;

@Tag(name = "회원 인증", description = "로그인, 회원가입, 로그인 상태 확인")
@RestController
public class AuthController {

    private final AuthService authService;
    private final AuthTokenCookieProvider authTokenCookieProvider;

    public AuthController(final AuthService authService, final AuthTokenCookieProvider authTokenCookieProvider) {
        this.authService = authService;
        this.authTokenCookieProvider = authTokenCookieProvider;
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid final LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        ResponseCookie cookie = authTokenCookieProvider.generate(loginResponse.tokenValue());
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @Operation(summary = "로그인 상태 확인", description = "요청을 보낸 유저가 로그인 상태인지 확인")
    @GetMapping("/login/check")
    public ResponseEntity<CheckLoginResponse> checkLogin(final LoginMember member) {
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new CheckLoginResponse("unauthorized"));
        }
        return ResponseEntity.ok(new CheckLoginResponse(member.name()));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = authTokenCookieProvider.generateExpired();
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}

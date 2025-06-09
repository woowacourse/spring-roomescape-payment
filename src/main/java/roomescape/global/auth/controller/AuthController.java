package roomescape.global.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.auth.domain.dto.TokenRequestDto;
import roomescape.global.auth.domain.dto.TokenResponseDto;
import roomescape.global.auth.service.AuthService;
import roomescape.user.domain.dto.UserResponseDto;

import java.time.Duration;

@Tag(name = "인증 API", description = "인증 API 입니다.")
@RestController
public class AuthController {

    private static final String TOKEN_NAME_FIELD = "token";
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "회원가입", description = "계정을 생성하는 회원가입 API입니다.")
    @PostMapping("/signin")
    public ResponseEntity<Void> login(@RequestBody TokenRequestDto tokenRequestDto) {
        TokenResponseDto tokenResponseDto = authService.login(tokenRequestDto);
        ResponseCookie cookie = ResponseCookie
                .from(TOKEN_NAME_FIELD, tokenResponseDto.accessToken())
                .path("/")
                .httpOnly(true)
                .secure(false)
                .maxAge(Duration.ofDays(30))
                .sameSite("Lax")
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    @Operation(summary = "로그인 확인", description = "현재 로그인 된 사용자 정보를 조회합니다.")
    @GetMapping("/login/check")
    public ResponseEntity<UserResponseDto> checkAuth(@CookieValue(name = TOKEN_NAME_FIELD) String token) {
        UserResponseDto userResponseDto = authService.findMemberByToken(token);
        return ResponseEntity.ok().body(userResponseDto);
    }

    @Operation(summary = "로그아웃", description = "만료된 쿠키를 설정하여 계정을 로그아웃합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = TOKEN_NAME_FIELD) String token) {
        authService.findMemberByToken(token);

        ResponseCookie cookie = ResponseCookie
                .from(TOKEN_NAME_FIELD, "")
                .domain("localhost")
                .path("/")
                .httpOnly(true)
                .secure(false)
                .maxAge(Duration.ofDays(0))
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }
}

package roomescape.presentation;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.Authenticated;
import roomescape.dto.request.LoginRequest;
import roomescape.dto.response.AuthenticatedUserResponse;
import roomescape.service.AuthService;

@Tag(name = "유저 인증 API", description = "유저 인증 API 입니다.")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final int cookieMaxAge;

    public AuthController(
            AuthService authService,
            @Value("${auth.cookie.max-age}") int cookieMaxAge
    ) {
        this.authService = authService;
        this.cookieMaxAge = cookieMaxAge;
    }

    @Operation(summary = "로그인", description = "유저가 로그인하면 인증 토큰을 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequest request) {
        String token = authService.createToken(request);
        ResponseCookie cookie = ResponseCookie.from("token")
                .value(token)
                .httpOnly(true)
                .maxAge(cookieMaxAge)
                .path("/")
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    @Operation(summary = "유저 인증 확인", description = "유저가 인증된 상태인지 확인하고 인증 정보를 반환합니다.")
    @GetMapping("/check")
    public AuthenticatedUserResponse getAuthenticatedUser(@Authenticated Long memberId) {
        return authService.getAuthenticatedUser(memberId);
    }

    @Operation(summary = "로그아웃", description = "유저가 로그아웃하면 인증 토큰을 만료시킵니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from("token")
                .httpOnly(true)
                .maxAge(0)
                .path("/")
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }
}

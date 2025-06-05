package roomescape.member.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.auth.annotation.AuthenticationPrincipal;
import roomescape.global.auth.dto.LoginMember;
import roomescape.global.auth.util.CookieUtil;
import roomescape.member.dto.request.LoginRequest;
import roomescape.member.dto.response.LoginResponse;
import roomescape.member.service.AuthService;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @Operation(summary = "로그인", description = "유효한 회원 인증 정보를 통해 로그인하고 토큰을 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse response
    ) {
        String token = authService.login(request);
        Cookie cookie = cookieUtil.createCookie("token", token);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그아웃", description = "토큰 쿠키를 만료하여 로그아웃 처리합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletResponse response
    ) {
        Cookie cookie = cookieUtil.expireCookie("token", "");
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인 상태 확인", description = "현재 로그인 상태를 확인합니다.")
    @GetMapping("/check")
    public ResponseEntity<LoginResponse> checkLogin(
            @AuthenticationPrincipal LoginMember loginMember
    ) {
        LoginResponse response = LoginResponse.from(loginMember);
        return ResponseEntity.ok().body(response);
    }
}

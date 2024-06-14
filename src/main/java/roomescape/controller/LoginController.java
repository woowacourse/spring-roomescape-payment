package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.config.CookieExtractor;
import roomescape.domain.LoginMember;
import roomescape.dto.LoginRequest;
import roomescape.dto.LoginResponse;
import roomescape.service.LoginService;

@Tag(name = "로그인 API", description = "로그인 API 입니다.")
@RestController
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Operation(summary = "로그인", description = "사용자가 로그인을 합니다.")
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        String loginToken = loginService.getLoginToken(loginRequest);
        Cookie accessTokenCookie = new Cookie("token", loginToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그아웃", description = "사용자가 로그아웃을 합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie accessTokenCookie = CookieExtractor.getTokenCookie(request);
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인 확인", description = "로그인한 사용자를 확인합니다.")
    @GetMapping("/login/check")
    public ResponseEntity<LoginResponse> loginCheck(@Authenticated LoginMember loginMember) {
        return ResponseEntity.ok(new LoginResponse(loginMember.getName()));
    }
}

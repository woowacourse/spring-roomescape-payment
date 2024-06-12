package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import roomescape.config.CookieExtractor;
import roomescape.dto.request.LoginMemberRequest;
import roomescape.dto.request.LoginRequest;
import roomescape.dto.response.LoginResponse;
import roomescape.service.LoginService;

@Controller
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/login")
    public String loginView() {
        return "login";
    }

    @Operation
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        String loginToken = loginService.getLoginToken(loginRequest);
        Cookie accessTokenCookie = new Cookie("token", loginToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie accessTokenCookie = CookieExtractor.getTokenCookie(request);
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);

        return ResponseEntity.ok().build();
    }

    @Operation
    @GetMapping("/login/check")
    public ResponseEntity<LoginResponse> loginCheck(@Parameter(description = "로그인한 멤버 정보") @Authenticated LoginMemberRequest loginMemberRequest) {
        return ResponseEntity.ok(LoginResponse.from(loginMemberRequest));
    }
}

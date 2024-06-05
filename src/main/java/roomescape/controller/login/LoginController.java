package roomescape.controller.login;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.config.auth.LoginMember;
import roomescape.config.auth.RoleAllowed;
import roomescape.domain.member.Member;
import roomescape.service.login.LoginService;
import roomescape.service.login.dto.LoginCheckResponse;
import roomescape.service.login.dto.LoginRequest;
import roomescape.service.login.dto.SignupRequest;
import roomescape.service.login.dto.SignupResponse;

import java.net.URI;

@RestController
public class LoginController {
    private final LoginService loginService;
    private final AuthCookieHandler authCookieHandler;

    public LoginController(LoginService loginService, AuthCookieHandler authCookieHandler) {
        this.loginService = loginService;
        this.authCookieHandler = authCookieHandler;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        String token = loginService.login(request);
        Cookie cookie = authCookieHandler.createCookie(token);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @RoleAllowed
    @GetMapping("/login/check")
    public ResponseEntity<LoginCheckResponse> loginCheck(@LoginMember Member member) {
        LoginCheckResponse response = loginService.loginCheck(member);
        return ResponseEntity.ok().body(response);
    }

    @RoleAllowed
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie cookie = authCookieHandler.deleteCookie();
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody @Valid SignupRequest request) {
        SignupResponse response = loginService.signup(request);
        return ResponseEntity.created(URI.create("/members/" + response.getId())).body(response);
    }
}

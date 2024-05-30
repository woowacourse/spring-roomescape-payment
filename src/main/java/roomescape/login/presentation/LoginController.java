package roomescape.login.presentation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.Authenticated;
import roomescape.global.util.CookieUtils;
import roomescape.login.dto.Accessor;
import roomescape.login.dto.LoginCheckResponse;
import roomescape.login.dto.LoginRequest;
import roomescape.login.infrastructure.Token;
import roomescape.login.service.AuthService;

@RestController
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Token issuedToken = authService.login(loginRequest);
        response.addCookie(CookieUtils.createTokenCookie(issuedToken));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/login/check")
    public ResponseEntity<LoginCheckResponse> checkLogin(@Authenticated Accessor accessor) {
        return ResponseEntity.ok().body(authService.checkLogin(accessor));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.clearTokenCookie(response);
        return ResponseEntity.ok().build();
    }
}

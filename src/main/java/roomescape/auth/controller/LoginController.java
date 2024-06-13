package roomescape.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoggedInMember;
import roomescape.auth.dto.LoginRequest;
import roomescape.auth.dto.LoginResponse;
import roomescape.auth.service.AuthService;

@Tag(name = "로그인 API")
@RestController
public class LoginController {

    private final TokenCookieManager tokenCookieManager;
    private final AuthService authService;

    public LoginController(TokenCookieManager tokenCookieManager, AuthService authService) {
        this.tokenCookieManager = tokenCookieManager;
        this.authService = authService;
    }

    @Operation(summary = "로그인 요청", description = "사용자 id/pw로 로그인을 요청한다.")
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest request) {
        String token = authService.createToken(request);
        ResponseCookie cookie = tokenCookieManager.createResponseCookie(token);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @Operation(summary = "로그인 체크", description = "로그인 중인 사용자를 체크한다.")
    @GetMapping("/login/check")
    public ResponseEntity<LoginResponse> loginCheck(HttpServletRequest request) {
        String token = tokenCookieManager.getToken(request.getCookies());
        if (token.isBlank()) {
            return ResponseEntity.noContent().build();
        }

        LoggedInMember loggedInMember = authService.findLoggedInMember(token);
        return ResponseEntity.ok()
                .body(new LoginResponse(loggedInMember.name()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = tokenCookieManager.createResponseCookie("");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}

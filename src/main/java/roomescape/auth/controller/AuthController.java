package roomescape.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginCheckResponse;
import roomescape.auth.dto.LoginRequest;
import roomescape.auth.infrastructure.util.CookieManager;
import roomescape.auth.service.AuthService;
import roomescape.global.exception.UnauthorizedException;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private static final String COOKIE_TOKEN = "token";

    private final AuthService authService;
    private final CookieManager cookieManager;

    @PostMapping("/login")
    public void login(@RequestBody @Valid final LoginRequest request, final HttpServletResponse response) {

        String token = authService.createToken(request);

        ResponseCookie cookie = cookieManager.generateLoginCookie(token);

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(final HttpServletResponse response) {
        final ResponseCookie cookie = cookieManager.generateLogoutCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @GetMapping("/login/check")
    public LoginCheckResponse checkLogin(@CookieValue(name = COOKIE_TOKEN, required = false) String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("토큰 쿠키가 존재하지 않습니다.");
        }
        return authService.checkLogin(token);
    }
}

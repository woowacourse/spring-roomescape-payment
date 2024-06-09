package roomescape.system.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.system.auth.annotation.MemberId;
import roomescape.system.auth.dto.LoginCheckResponse;
import roomescape.system.auth.dto.LoginRequest;
import roomescape.system.auth.jwt.dto.TokenDto;
import roomescape.system.auth.service.AuthService;
import roomescape.system.dto.response.ApiResponse;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<Void> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        TokenDto tokenInfo = authService.login(loginRequest);
        addCookieToResponse(new Cookie("accessToken", tokenInfo.accessToken()), response);
        return ApiResponse.success();
    }

    @GetMapping("/login/check")
    public ApiResponse<LoginCheckResponse> checkLogin(@MemberId Long memberId) {
        LoginCheckResponse response = authService.checkLogin(memberId);
        return ApiResponse.success(response);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Cookie cookie = getTokenCookie(request);
        cookie.setValue(null);
        cookie.setMaxAge(0);
        addCookieToResponse(cookie, response);
        return ApiResponse.success();
    }

    private Cookie getTokenCookie(HttpServletRequest request) {
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("accessToken")) {
                return cookie;
            }
        }
        return new Cookie("accessToken", null);
    }

    private void addCookieToResponse(Cookie cookie, HttpServletResponse response) {
        cookie.setHttpOnly(true);

        response.addCookie(cookie);
    }
}

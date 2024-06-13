package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.service.AuthService;
import roomescape.config.LoginMemberConverter;
import roomescape.dto.response.member.CheckMemberResponse;
import roomescape.dto.LoginMember;
import roomescape.dto.request.member.TokenRequest;
import roomescape.dto.response.member.TokenResponse;

@Tag(name = "사용자 로그인 API", description = "사용자 로그인 관련 API 입니다.")
@RestController
public class LoginController {
    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "사용자 로그인 API")
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody TokenRequest tokenRequest, HttpServletResponse response) {
        TokenResponse token = authService.createToken(tokenRequest);
        Cookie cookie = authService.addTokenToCookie(token.accessToken());
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 체크 API")
    @GetMapping("/login/check")
    public ResponseEntity<CheckMemberResponse> check(@LoginMemberConverter LoginMember loginMember) {
        return ResponseEntity.ok(new CheckMemberResponse(loginMember.name()));
    }

    @Operation(summary = "사용자 로그아웃 API")
    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        response.addCookie(new Cookie("token", ""));
    }
}

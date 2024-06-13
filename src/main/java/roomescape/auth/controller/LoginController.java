package roomescape.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.auth.annotation.Authenticated;
import roomescape.auth.config.CookieExtractor;
import roomescape.auth.dto.LoginRequest;
import roomescape.auth.dto.LoginResponse;
import roomescape.auth.service.LoginService;
import roomescape.member.domain.LoginMember;

@Controller
@Tag(name = "Login", description = "Login API")
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/login")
    public String loginView() {
        return "/login";
    }

    @Operation(summary = "사용자 로그인", description = "사용자 로그인을 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 로그인 성공"),
            @ApiResponse(responseCode = "400", description = "사용자 로그인 실패")})
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest,
                                      @Parameter(hidden = true) HttpServletResponse response) {
        String loginToken = loginService.getLoginToken(loginRequest);
        Cookie accessTokenCookie = new Cookie("token", loginToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 로그아웃", description = "사용자 로그아웃을 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 로그아웃 성공"),
            @ApiResponse(responseCode = "400", description = "사용자 로그아웃 실패")})
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(hidden = true) HttpServletRequest request,
            @Parameter(hidden = true) HttpServletResponse response) {
        Cookie accessTokenCookie = CookieExtractor.getTokenCookie(request);
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 로그인 검증", description = "사용자 로그인을 검증 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 로그인 검증 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "사용자 로그인 검증 실패")})
    @GetMapping("/login/check")
    public ResponseEntity<LoginResponse> loginCheck(@Parameter(hidden = true) @Authenticated LoginMember loginMember) {
        return ResponseEntity.ok(new LoginResponse(loginMember.getName()));
    }
}

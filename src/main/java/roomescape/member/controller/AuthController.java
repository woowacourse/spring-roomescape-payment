package roomescape.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "인증", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @Operation(summary = "로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    @SecurityRequirements(value = {})
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

    @Operation(summary = "로그아웃")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletResponse response
    ) {
        Cookie cookie = cookieUtil.expireCookie("token", "");
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인 체크")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    @GetMapping("/check")
    public ResponseEntity<LoginResponse> checkLogin(
            @Parameter(hidden = true) @AuthenticationPrincipal LoginMember loginMember
    ) {
        LoginResponse response = LoginResponse.from(loginMember);
        return ResponseEntity.ok().body(response);
    }
}

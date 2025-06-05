package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Duration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.configuration.annotation.Authority;
import roomescape.configuration.annotation.RequiredAccessToken;
import roomescape.configuration.annotation.docs.DocsAuthorizationExceptionResponse;
import roomescape.configuration.annotation.docs.DocsSuccessResponse;
import roomescape.domain.Role;
import roomescape.dto.business.AccessTokenContent;
import roomescape.dto.request.LoginRequest;
import roomescape.dto.response.AccessTokenResponse;
import roomescape.dto.response.MemberProfileResponse;
import roomescape.service.command.AuthService;

@Tag(name = "AuthController", description = "인증/인가 관련 API")
@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Login", description = "로그인 수행")
    @DocsSuccessResponse
    @ApiResponse(responseCode = "400", description = "로그인 정보가 올바르지 않은 경우",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        AccessTokenResponse accessTokenResponse = authService.login(loginRequest);
        ResponseCookie cookie = ResponseCookie
                .from("access", accessTokenResponse.accessToken())
                .path("/")
                .httpOnly(true)
                .secure(false)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @Operation(summary = "Check Login", description = "로그인 여부 체크")
    @DocsSuccessResponse
    @DocsAuthorizationExceptionResponse
    @GetMapping("/login/check")
    @Authority(Role.GENERAL)
    public MemberProfileResponse checkLogin(
            @RequiredAccessToken AccessTokenContent accessTokenContent
    ) {
        return new MemberProfileResponse(accessTokenContent);
    }

    @Operation(summary = "Logout", description = "로그인 아웃")
    @DocsSuccessResponse
    @DocsAuthorizationExceptionResponse
    @PostMapping("/logout")
    @Authority(Role.GENERAL)
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie
                .from("access", "")
                .path("/")
                .httpOnly(true)
                .secure(false)
                .maxAge(Duration.ofDays(0))
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}

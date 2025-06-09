package roomescape.mvc.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import roomescape.annotation.Authority;
import roomescape.annotation.RequiredAccessToken;
import roomescape.mvc.auth.dto.AccessTokenContent;
import roomescape.mvc.auth.request.LoginRequest;
import roomescape.mvc.auth.response.AccessTokenResponse;
import roomescape.mvc.auth.response.CheckLoginResponse;
import roomescape.mvc.auth.service.AuthService;
import roomescape.mvc.member.domain.Role;

@Tag(name = "AuthController", description = "인증/인가 관련 API")
@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Login", description = "로그인 수행")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "access 토큰이 올바르지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "권한이 맞지 않는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "로그인 정보가 올바르지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = CheckLoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "access 토큰이 올바르지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "권한이 맞지 않는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "로그인 정보가 올바르지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/login/check")
    @Authority(Role.GENERAL)
    public CheckLoginResponse checkLogin(
            @RequiredAccessToken AccessTokenContent accessTokenContent
    ) {
        return new CheckLoginResponse(accessTokenContent);
    }

    @Operation(summary = "Logout", description = "로그인 아웃")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "access 토큰이 올바르지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "권한이 맞지 않는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
    })
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

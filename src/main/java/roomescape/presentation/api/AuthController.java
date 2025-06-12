package roomescape.presentation.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.AuthService;
import roomescape.presentation.AuthenticationPrincipal;
import roomescape.presentation.dto.request.LoginMember;
import roomescape.presentation.dto.request.LoginRequest;
import roomescape.presentation.dto.response.ErrorResponse;
import roomescape.presentation.dto.response.MemberResponse;

@Tag(name = "로그인 관련 기능 API")
@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 이메일인 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "올바르지 않은 비밀번호인 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse response) {
        String accessToken = authService.login(loginRequest);
        createCookie(response, accessToken);
        return ResponseEntity.ok().build();
    }

    private void createCookie(HttpServletResponse response, String accessToken) {
        Cookie cookie = new Cookie("token", accessToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);
    }

    @GetMapping("/login/check")
    @Operation(summary = "로그인 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "401", description = "쿠키가 존재하지 않거나 토큰이 올바르지 않은 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MemberResponse> checkLogin(
            @Parameter(hidden = true) @AuthenticationPrincipal LoginMember loginMember
    ) {
        MemberResponse memberResponse = MemberResponse.from(loginMember);
        return ResponseEntity.ok().body(memberResponse);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }
}

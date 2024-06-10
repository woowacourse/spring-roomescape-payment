package roomescape.auth.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.Authenticated;
import roomescape.auth.dto.Accessor;
import roomescape.auth.dto.LoginCheckResponse;
import roomescape.auth.dto.LoginRequest;
import roomescape.auth.infrastructure.Token;
import roomescape.auth.service.AuthService;
import roomescape.global.util.CookieUtils;

@RestController
@Tag(name = "Authentication API", description = "로그인 관련 API")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "사용자 로그인 API", description = "사용자 로그인 시 요청합니다.")
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Token issuedToken = authService.login(loginRequest);
        response.addCookie(CookieUtils.createTokenCookie(issuedToken));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 로그인 확인 API", description = "사용자의 로그인 상태를 확인합니다.")
    @ApiResponses(value = {

    })
    @GetMapping("/login/check")
    public ResponseEntity<LoginCheckResponse> checkLogin(@Authenticated Accessor accessor) {
        return ResponseEntity.ok().body(authService.checkLogin(accessor));
    }

    @Operation(summary = "사용자 로그아웃 API", description = "사용자 로그아웃 시 요청합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.clearTokenCookie(response);
        return ResponseEntity.ok().build();
    }
}

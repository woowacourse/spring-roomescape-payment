package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.auth.CurrentMember;
import roomescape.dto.auth.LoginInfo;
import roomescape.dto.auth.LoginRequest;
import roomescape.dto.member.MemberNameResponse;
import roomescape.service.AuthService;

@Tag(name = "인증 API", description = "로그인, 로그아웃, 로그인 체크 등의 인증 관련 API입니다.")
@RestController
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "로그인", description = "사용자 로그인 및 JWT 토큰 발급 (쿠키 저장)")
    @ApiResponse(responseCode = "200", description = "로그인 성공, 쿠키에 토큰 저장")
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "로그인 요청 정보", required = true)
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        String token = authService.publishLoginToken(loginRequest);
        Cookie cookie = createCookie(token);
        response.addCookie(cookie);

        log.info("Login attempt: {}", loginRequest.email());
        return ResponseEntity.ok().build();
    }

    private Cookie createCookie(String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

    @Operation(summary = "로그인 상태 확인", description = "현재 로그인된 사용자의 이름을 반환합니다.")
    @GetMapping("/login/check")
    public ResponseEntity<MemberNameResponse> checkLogin(
            @Parameter(hidden = true) @CurrentMember LoginInfo loginMember) {
        MemberNameResponse memberResponse = new MemberNameResponse(loginMember.name());
        return ResponseEntity.ok(memberResponse);
    }

    @Operation(summary = "로그아웃", description = "사용자의 로그인 토큰 쿠키를 제거합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie cookie = createCookie(null);
        response.addCookie(cookie);

        log.info("User logged out");
        return ResponseEntity.ok().build();
    }
}

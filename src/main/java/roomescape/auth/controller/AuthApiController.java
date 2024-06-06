package roomescape.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginMember;
import roomescape.auth.dto.LoginRequest;
import roomescape.auth.service.AuthService;
import roomescape.member.dto.MemberResponse;
import roomescape.util.CookieUtils;

@Tag(name = "인증 API", description = "인증 관련 API 입니다.")
@RestController
public class AuthApiController {

    private final AuthService authService;

    public AuthApiController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "로그인 API")
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        String token = authService.createMemberToken(loginRequest);
        CookieUtils.setCookieByToken(response, token);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인 상태 확인 API", description = "회원의 로그인 상태를 확인 합니다.")
    @GetMapping("/login/member")
    public ResponseEntity<MemberResponse> findMemberNameByLoginMember(LoginMember loginMember) {
        MemberResponse memberResponse = authService.findMemberNameByLoginMember(loginMember);

        return ResponseEntity.ok(memberResponse);
    }

    @Operation(summary = "로그아웃 API")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        CookieUtils.clearTokenAndCookie(response);

        return ResponseEntity.ok().build();
    }
}

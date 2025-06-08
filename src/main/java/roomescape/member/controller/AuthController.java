package roomescape.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.auth.PermitAll;
import roomescape.member.auth.jwt.JwtTokenExtractor;
import roomescape.member.controller.dto.LoginCheckResponse;
import roomescape.member.controller.dto.LoginRequest;
import roomescape.member.controller.dto.MemberInfoResponse;
import roomescape.member.controller.dto.SignupRequest;
import roomescape.member.service.AuthService;

@RequiredArgsConstructor
@RestController
//@Tag(name = "Auth", description = "인증을 위한 api")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenExtractor jwtTokenExtractor;

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인을 처리하고, 성공 시 jwt 토큰을 반환합니다.")
    @PermitAll
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", "token=" + token + "; Path=/; HttpOnly");
        headers.add("Keep-Alive", "timeout=60");

        return ResponseEntity.ok().headers(headers).build();
    }

    @Operation(summary = "로그아웃", description = "토큰 쿠키를 삭제하여 로그아웃 처리합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", "token=; Path=/; HttpOnly; Max-Age=0");

        return ResponseEntity.ok().headers(headers).build();
    }

    @Operation(summary = "로그인 확인", description = "현재 jwt 토큰이 유효한지 확인하여 로그인 여부를 확인합니다.")
    @GetMapping("/login/check")
    public ResponseEntity<LoginCheckResponse> checkLogin(HttpServletRequest request) {
        final String token = jwtTokenExtractor.extractTokenFromCookie(request.getCookies());
        return ResponseEntity.ok(authService.checkLogin(token));
    }

    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
    @PermitAll
    @PostMapping("/signup")
    public ResponseEntity<MemberInfoResponse> signup(@RequestBody SignupRequest signupRequest) {
        return ResponseEntity.ok(authService.signup(signupRequest));
    }
}

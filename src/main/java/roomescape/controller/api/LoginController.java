package roomescape.controller.api;

import java.time.Duration;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.controller.dto.request.LoginRequest;
import roomescape.controller.dto.response.LoginCheckResponse;
import roomescape.domain.member.Member;
import roomescape.global.argumentresolver.AuthenticationPrincipal;
import roomescape.service.LoginService;

@Tag(name = "Login", description = "로그인 관련 API")
@RestController
@RequestMapping("/login")
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Operation(summary = "로그인 확인", description = "로그인 정보를 확인할 수 있다.")
    @GetMapping("/check")
    public ResponseEntity<LoginCheckResponse> checkLogin(@AuthenticationPrincipal Member member) {
        return ResponseEntity.ok(new LoginCheckResponse(member.getName(), member.getRole()));
    }

    @Operation(summary = "로그인", description = "로그인 할 수 있다.")
    @PostMapping
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request) {
        String token = loginService.login(request);

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofMinutes(30))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}

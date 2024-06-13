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

import roomescape.controller.api.docs.LoginApiDocs;
import roomescape.controller.dto.request.LoginRequest;
import roomescape.controller.dto.response.LoginCheckResponse;
import roomescape.domain.member.Member;
import roomescape.global.argumentresolver.AuthenticationPrincipal;
import roomescape.service.LoginService;

@RestController
@RequestMapping("/login")
public class LoginController implements LoginApiDocs {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/check")
    public ResponseEntity<LoginCheckResponse> checkLogin(@AuthenticationPrincipal Member member) {
        return ResponseEntity.ok(new LoginCheckResponse(member.getName(), member.getRole()));
    }

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

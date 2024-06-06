package roomescape.web.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.MemberService;
import roomescape.application.dto.request.member.LoginRequest;
import roomescape.application.dto.request.member.MemberInfo;
import roomescape.application.dto.response.member.MemberResponse;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private static final String TOKEN_COOKIE_KEY_NAME = "token";

    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequest request) {
        String token = memberService.login(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, token)
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = createCookieWithEmptyToken();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @GetMapping("/login/check")
    public ResponseEntity<MemberResponse> checkAuthenticated(
            MemberInfo memberInfo
    ) {
        MemberResponse response = new MemberResponse(memberInfo.id(), memberInfo.name());
        return ResponseEntity.ok().body(response);
    }

    private ResponseCookie createCookieWithEmptyToken() {
        return ResponseCookie.from(TOKEN_COOKIE_KEY_NAME, "")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .build();
    }
}

package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.Login;
import roomescape.controller.dto.TokenRequest;
import roomescape.service.AuthService;
import roomescape.service.MemberService;
import roomescape.service.dto.request.LoginMember;
import roomescape.service.dto.response.MemberResponse;

@RestController
public class LoginController {

    private final MemberService memberService;
    private final AuthService authService;

    public LoginController(MemberService memberService, AuthService authService) {
        this.memberService = memberService;
        this.authService = authService;
    }

    @Operation(summary = "로그인 API", description = "로그인한다.")
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody TokenRequest tokenRequest) {
        MemberResponse memberResponse = memberService.findByEmailAndPassword(tokenRequest.email(), tokenRequest.password());
        String accessToken = authService.createToken(memberResponse);

        ResponseCookie cookie = ResponseCookie
                .from(authService.getTokenName(), accessToken)
                .httpOnly(true)
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    @Operation(summary = "로그인 체크 API", description = "로그인된 사용자 정보를 반환한다.")
    @GetMapping("/login/check")
    public ResponseEntity<MemberResponse> findMyInfo(@Login LoginMember loginMember) {
        MemberResponse memberResponse = loginMember.toMemberResponse();

        return ResponseEntity.ok()
                .body(memberResponse);
    }

    @Operation(summary = "로그아웃 API", description = "로그아웃한다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie
                .from(authService.getTokenName(), null)
                .httpOnly(true)
                .path("/")
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }
}

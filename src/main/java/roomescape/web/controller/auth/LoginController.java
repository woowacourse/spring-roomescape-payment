package roomescape.web.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.login.LoginCheckResponse;
import roomescape.dto.login.LoginMember;
import roomescape.dto.login.LoginRequest;
import roomescape.dto.member.MemberResponse;
import roomescape.dto.token.TokenDto;
import roomescape.service.auth.AuthService;
import roomescape.service.member.MemberService;

@Tag(name = "로그인")
@RestController
class LoginController {

    private final AuthService authService;
    private final MemberService memberService;

    public LoginController(AuthService authService, MemberService memberService) {
        this.authService = authService;
        this.memberService = memberService;
    }

    @Operation(summary = "로그인", description = "로그인 정보로 로그인한다.")
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest request) {
        TokenDto loginToken = authService.login(request);
        ResponseCookie cookie = createResponseCookie(loginToken);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    @Operation(summary = "로그인 확인", description = "로그인한 사용자를 확인한다.")
    @GetMapping("/login/check")
    public ResponseEntity<LoginCheckResponse> checkLogin(LoginMember loginMember) {
        MemberResponse memberResponse = memberService.getMemberById(loginMember.id());
        LoginCheckResponse response = new LoginCheckResponse(memberResponse.name());
        return ResponseEntity.ok(response);
    }

    private ResponseCookie createResponseCookie(TokenDto loginToken) {
        return ResponseCookie
                .from("token", loginToken.accessToken())
                .path("/")
                .httpOnly(true)
                .build();
    }
}

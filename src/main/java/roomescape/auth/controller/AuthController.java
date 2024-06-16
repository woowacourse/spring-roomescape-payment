package roomescape.auth.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.annotation.LoginMemberId;
import roomescape.auth.domain.Token;
import roomescape.auth.dto.LoginRequest;
import roomescape.auth.provider.CookieProvider;
import roomescape.auth.service.AuthService;
import roomescape.member.dto.MemberLoginCheckResponse;
import roomescape.member.service.MemberService;

@Tag(name = "auth 컨트롤러", description = "로그인한 사용자에게 쿠키를 부여하고 로그인 정보가 실제로 존재하는지 확인한다.")
@RestController
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;

    public AuthController(AuthService authService, MemberService memberService) {
        this.authService = authService;
        this.memberService = memberService;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {
        Token token = authService.login(loginRequest);
        ResponseCookie responseCookie = CookieProvider.setCookieFrom(token);

        return ResponseEntity.ok()
                .header("Set-Cookie", responseCookie.toString())
                .build();
    }

    @GetMapping("/login/check")
    public MemberLoginCheckResponse loginCheck(@LoginMemberId Long id) {
        return memberService.findLoginMemberInfo(id);
    }
}

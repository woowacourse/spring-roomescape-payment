package roomescape.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import roomescape.controller.dto.LoginRequest;
import roomescape.infrastructure.Login;
import roomescape.service.AuthService;
import roomescape.service.MemberService;
import roomescape.service.dto.LoginMember;
import roomescape.service.dto.MemberResponse;

@Controller
public class LoginController {

    private final MemberService memberService;
    private final AuthService authService;

    public LoginController(MemberService memberService, AuthService authService) {
        this.memberService = memberService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public void login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        MemberResponse memberResponse = memberService.findByEmailAndPassword(loginRequest.email(), loginRequest.password());
        String accessToken = authService.createToken(memberResponse);

        Cookie cookie = new Cookie(authService.getTokenName(), accessToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    @GetMapping("/login/check")
    public ResponseEntity<MemberResponse> findMyInfo(@Login LoginMember loginMember) {
        MemberResponse memberResponse = loginMember.toMemberResponse();

        return ResponseEntity.ok()
                .body(memberResponse);
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie(authService.getTokenName(), null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}

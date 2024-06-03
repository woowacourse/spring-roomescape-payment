package roomescape.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import roomescape.auth.Login;
import roomescape.controller.dto.TokenRequest;
import roomescape.service.auth.AuthService;
import roomescape.service.dto.request.LoginMember;
import roomescape.service.dto.response.MemberResponse;
import roomescape.service.reservation.MemberService;

@Controller
public class LoginController {

    private final MemberService memberService;
    private final AuthService authService;

    public LoginController(MemberService memberService, AuthService authService) {
        this.memberService = memberService;
        this.authService = authService;
    }

    @GetMapping("/login")
    public String page() {
        return "login";
    }

    @PostMapping("/login")
    public void login(@RequestBody TokenRequest tokenRequest, HttpServletResponse response) {
        MemberResponse memberResponse = memberService.findByEmailAndPassword(tokenRequest.email(), tokenRequest.password());
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

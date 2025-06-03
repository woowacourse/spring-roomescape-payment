package roomescape.member.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.dto.SessionMember;
import roomescape.member.controller.dto.LoginCheckResponse;
import roomescape.member.controller.dto.LoginRequest;
import roomescape.member.domain.Member;

@RestController
@RequestMapping("/login")
public class LoginController {
    private final MemberService memberService;

    public LoginController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<Void> login(@RequestBody final LoginRequest request, final HttpSession session) {
        Member member = memberService.login(request);
        session.setAttribute("LOGIN_MEMBER", new SessionMember(member.getId(), member.getName(), member.getRole()));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check")
    public ResponseEntity<LoginCheckResponse> loginCheck(final HttpSession httpSession) {
        SessionMember sessionMember = (SessionMember) httpSession.getAttribute("LOGIN_MEMBER");
        LoginCheckResponse loginCheckResponse = new LoginCheckResponse(sessionMember.name().name());
        return ResponseEntity.ok(loginCheckResponse);
    }
}

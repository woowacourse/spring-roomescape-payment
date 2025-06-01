package roomescape.controller.user;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.member.Member;
import roomescape.dto.request.LoginRequest;
import roomescape.dto.request.MemberRegisterRequest;
import roomescape.dto.response.LoginResponse;
import roomescape.dto.response.MemberRegisterResponse;
import roomescape.service.auth.AuthService;
import roomescape.service.member.MemberService;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private static final String SESSION_KEY = "id";
    private static final int SESSION_TIMEOUT_SECOND = 60 * 60;

    private final AuthService authService;
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<MemberRegisterResponse> register(@RequestBody MemberRegisterRequest request) {
        MemberRegisterResponse response = memberService.addMember(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequest loginRequest, HttpSession session) {
        Long memberId = authService.authenticate(loginRequest);
        session.setAttribute(SESSION_KEY, memberId);
        session.setMaxInactiveInterval(SESSION_TIMEOUT_SECOND);
        authService.updateSessionIdByMemberId(memberId, session.getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/login/check")
    public ResponseEntity<LoginResponse> loginCheck(HttpSession session) {
        if (session.getAttribute(SESSION_KEY) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        final long id = (long) session.getAttribute(SESSION_KEY);
        Member member = memberService.getMemberById(id);
        LoginResponse response = new LoginResponse(member.getName());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

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
import roomescape.global.LoginInfo;
import roomescape.service.auth.AuthService;
import roomescape.service.helper.MemberHelper;
import roomescape.service.member.MemberService;

import javax.naming.AuthenticationException;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;
    private final MemberHelper memberHelper;

    @PostMapping("/auth/signup")
    public ResponseEntity<MemberRegisterResponse> register(@RequestBody MemberRegisterRequest request) {
        MemberRegisterResponse response = memberService.register(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequest loginRequest, HttpSession session) throws AuthenticationException {
        authService.login(loginRequest, session);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/auth/login/check")
    public ResponseEntity<LoginResponse> loginCheck(LoginInfo loginInfo) {
        Member member = memberHelper.getById(loginInfo.memberId());
        LoginResponse response = new LoginResponse(member.getName());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

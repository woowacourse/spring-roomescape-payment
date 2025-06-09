package roomescape.auth.presentation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.annotation.LoginMember;
import roomescape.auth.application.LoginService;
import roomescape.auth.dto.info.LoginMemberInfo;
import roomescape.auth.dto.request.LoginRequest;
import roomescape.auth.dto.response.LoginCheckResponse;
import roomescape.member.domain.Member;

@Slf4j
@RestController
@RequestMapping("/login")
public class MemberLoginController {

    private final LoginService loginService;

    public MemberLoginController(final LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping
    public ResponseEntity<Void> login(@RequestBody final LoginRequest request) {
        log.info("회원 로그인 요청: email={}", request.email());
        String token = loginService.createMemberToken(request);

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .path("/")
                .maxAge(1800000)
                .build();

        log.info("회원 로그인 성공: email={}", request.email());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @GetMapping("/check")
    public ResponseEntity<LoginCheckResponse> checkLogin(@LoginMember final LoginMemberInfo info) {
        log.debug("회원 로그인 상태 확인 요청: memberId={}", info.id());
        Member member = loginService.findByMemberId(info.id());

        log.debug("회원 로그인 확인 완료: memberName={}", member.getName());
        return ResponseEntity.ok().body(new LoginCheckResponse(member.getName()));
    }
}

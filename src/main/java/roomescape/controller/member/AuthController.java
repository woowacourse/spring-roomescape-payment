package roomescape.controller.member;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.dto.TokenResponse;
import roomescape.controller.member.dto.CookieMemberResponse;
import roomescape.controller.member.dto.LoginMember;
import roomescape.controller.member.dto.MemberLoginRequest;
import roomescape.service.MemberService;

@RestController
@RequestMapping("/login")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final MemberService memberService;

    public AuthController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid final MemberLoginRequest memberLoginRequest,
                                               final HttpServletResponse response) {
        final TokenResponse token = memberService.createToken(memberLoginRequest);

        final Cookie cookie = new Cookie("token", token.assessToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(3600000);
        response.addCookie(cookie);
        log.info("로그인 email={}", memberLoginRequest.email());
        return ResponseEntity.ok()
                .body(token);
    }

    @GetMapping("/check")
    public ResponseEntity<CookieMemberResponse> check(@Valid final LoginMember loginMember) {
        if (loginMember != null) {
            return ResponseEntity.ok(new CookieMemberResponse(loginMember.name()));
        }
        return ResponseEntity.ok(CookieMemberResponse.NON_LOGIN);
    }
}

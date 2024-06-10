package roomescape.core.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.core.dto.auth.TokenRequest;
import roomescape.core.dto.auth.TokenResponse;
import roomescape.core.dto.member.MemberRequest;
import roomescape.core.dto.member.MemberResponse;
import roomescape.core.service.CookieService;
import roomescape.core.service.MemberService;

@RestController
public class MemberController {
    private final MemberService memberService;
    private final CookieService cookieService = new CookieService();

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> loginProcess(@RequestBody final TokenRequest request,
                                             final HttpServletResponse response) {
        final TokenResponse tokenResponse = memberService.createToken(request);
        final Cookie cookie = cookieService.createCookie(tokenResponse);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/login/check")
    public ResponseEntity<MemberResponse> checkLogin(final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        final String token = cookieService.extractCookies(cookies);
        final MemberResponse response = memberService.findMemberByToken(token);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(final HttpServletResponse response) {
        final Cookie cookie = cookieService.createEmptyCookie();
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/members")
    public ResponseEntity<MemberResponse> signupProcess(@RequestBody final MemberRequest request) {
        final MemberResponse response = memberService.create(request);

        return ResponseEntity.created(URI.create("/members/" + response.getId()))
                .body(response);
    }

    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> findMembers() {
        final List<MemberResponse> memberResponses = memberService.findAll();
        return ResponseEntity.ok().body(memberResponses);
    }
}

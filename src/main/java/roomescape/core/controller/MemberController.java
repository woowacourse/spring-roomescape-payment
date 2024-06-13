package roomescape.core.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import roomescape.core.dto.auth.TokenRequest;
import roomescape.core.dto.auth.TokenResponse;
import roomescape.core.dto.member.MemberRequest;
import roomescape.core.dto.member.MemberResponse;
import roomescape.core.service.CookieService;
import roomescape.core.service.MemberService;

@Tag(name = "회원 관리 API")
@Controller
public class MemberController {
    private final MemberService memberService;
    private final CookieService cookieService = new CookieService();

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/reservation")
    public String reservation() {
        return "reservation";
    }

    @GetMapping("/reservation-mine")
    public String findMyReservations() {
        return "reservation-mine";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @Operation(summary = "로그인 요청", description = "로그인에 성공하면 토큰을 쿠키에 반환합니다.")
    @PostMapping("/login")
    public ResponseEntity<Void> loginProcess(@RequestBody final TokenRequest request,
                                             final HttpServletResponse response) {
        final TokenResponse tokenResponse = memberService.createToken(request);
        final Cookie cookie = cookieService.createCookie(tokenResponse);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인 확인", description = "로그인 API 실행이 선행되어야 합니다.")
    @GetMapping("/login/check")
    public ResponseEntity<MemberResponse> checkLogin(final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        final String token = cookieService.extractCookies(cookies);
        final MemberResponse response = memberService.findMemberByToken(token);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "로그아웃", description = "로그인 API 실행이 선행되어야 합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(final HttpServletResponse response) {
        final Cookie cookie = cookieService.createEmptyCookie();
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원가입")
    @PostMapping("/members")
    public ResponseEntity<MemberResponse> signupProcess(@RequestBody final MemberRequest request) {
        final MemberResponse result = memberService.create(request);

        return ResponseEntity.created(URI.create("/members/" + result.getId()))
                .body(result);
    }

    @Operation(summary = "모든 회원 조회")
    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> findMembers() {
        final List<MemberResponse> memberResponses = memberService.findAll();
        return ResponseEntity.ok().body(memberResponses);
    }
}

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

@Tag(name = "사용자 API", description = "사용자 페이지 관련 API 입니다.")
@Controller
public class MemberController {
    private final MemberService memberService;
    private final CookieService cookieService = new CookieService();

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "로그인 회원 예약 페이지")
    @GetMapping("/reservation")
    public String reservation() {
        return "reservation";
    }

    @Operation(summary = "로그인 회원의 예약 목록 페이지")
    @GetMapping("/reservation-mine")
    public String findMyReservations() {
        return "reservation-mine";
    }

    @Operation(summary = "로그인 페이지")
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @Operation(summary = "로그인 API")
    @PostMapping("/login")
    public ResponseEntity<Void> loginProcess(@RequestBody final TokenRequest request,
                                             final HttpServletResponse response) {
        final TokenResponse tokenResponse = memberService.createToken(request);
        final Cookie cookie = cookieService.createCookie(tokenResponse);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인 확인 API")
    @GetMapping("/login/check")
    public ResponseEntity<MemberResponse> checkLogin(final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        final String token = cookieService.extractCookies(cookies);
        final MemberResponse response = memberService.findMemberByToken(token);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "로그아웃 API")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(final HttpServletResponse response) {
        final Cookie cookie = cookieService.createEmptyCookie();
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원가입 페이지")
    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @Operation(summary = "회원가입 API")
    @PostMapping("/members")
    public ResponseEntity<MemberResponse> signupProcess(@RequestBody final MemberRequest request) {
        final MemberResponse result = memberService.create(request);

        return ResponseEntity.created(URI.create("/members/" + result.getId()))
                .body(result);
    }

    @Operation(summary = "회원 목록 조회 API")
    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> findMembers() {
        final List<MemberResponse> memberResponses = memberService.findAll();
        return ResponseEntity.ok().body(memberResponses);
    }
}

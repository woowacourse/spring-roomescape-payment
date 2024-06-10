package roomescape.controller.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.CookieProvider;
import roomescape.dto.MemberResponse;
import roomescape.dto.auth.TokenRequest;
import roomescape.dto.auth.TokenResponse;
import roomescape.service.MemberService;

@Tag(name = "로그인 관련 API")
@RestController
public class AuthController {

    private final MemberService memberService;
    private final CookieProvider cookieProvider;

    @Autowired
    public AuthController(final MemberService memberService, final CookieProvider cookieProvider) {
        this.memberService = memberService;
        this.cookieProvider = cookieProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody final TokenRequest request, final HttpServletResponse response) {
        final TokenResponse tokenResponse = memberService.createToken(request);
        final Cookie cookie = cookieProvider.createCookie(tokenResponse.accessToken());
        response.addCookie(cookie);
        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/login/check")
    public ResponseEntity<MemberResponse> findMemberInfo(final HttpServletRequest request) {
        final String accessToken = cookieProvider.extractToken(request.getCookies());
        final MemberResponse memberResponse = memberService.findMemberByToken(accessToken);
        return ResponseEntity.ok(memberResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(final HttpServletResponse response) {
        response.addCookie(cookieProvider.expireCookie());
        return ResponseEntity.ok().build();
    }
}

package roomescape.member.presentation;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.argumentResolver.Login;
import roomescape.common.util.TokenCookieManager;
import roomescape.member.adaptor.MemberApiAdaptor;
import roomescape.member.docs.LoginCheckResponseDocs;
import roomescape.member.docs.LoginMemberDocs;
import roomescape.member.docs.LoginRequestDocs;
import roomescape.member.dto.request.LoginMember;
import roomescape.member.dto.request.LoginRequest;
import roomescape.member.dto.response.LoginCheckResponse;
import roomescape.member.service.LoginService;

@RestController
public class LoginController {

    private final TokenCookieManager tokenCookieManager;
    private final LoginService loginService;
    private final MemberApiAdaptor memberApiAdaptor;

    public LoginController(final TokenCookieManager tokenCookieManager, final LoginService loginService, final MemberApiAdaptor memberApiAdaptor) {
        this.tokenCookieManager = tokenCookieManager;
        this.loginService = loginService;
        this.memberApiAdaptor = memberApiAdaptor;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequestDocs requestDocs, HttpServletResponse response) {
        LoginRequest request = memberApiAdaptor.toLoginRequest(requestDocs);

        String token = loginService.loginAndReturnToken(request);
        tokenCookieManager.addTokenCookie(response, token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/login/check")
    public ResponseEntity<LoginCheckResponseDocs> loginCheck(@Login LoginMemberDocs memberDocs) {
        LoginMember loginMember = memberApiAdaptor.toLoginMember(memberDocs);

        String memberName = loginService.findMemberName(loginMember.id());
        LoginCheckResponse loginCheckResponse = new LoginCheckResponse(memberName);
        LoginCheckResponseDocs loginCheckResponseDocs = memberApiAdaptor.toLoginCheckResponseDocs(loginCheckResponse);
        return ResponseEntity.ok().body(loginCheckResponseDocs);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        tokenCookieManager.deleteTokenCookie(response);
        return ResponseEntity.ok().build();
    }
}

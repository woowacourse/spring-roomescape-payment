package roomescape.member.presentation;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.common.argumentResolver.Login;
import roomescape.common.util.TokenCookieManager;
import roomescape.member.dto.request.LoginMember;
import roomescape.member.dto.request.LoginRequest;
import roomescape.member.dto.response.LoginCheckResponse;
import roomescape.member.service.LoginService;

@RestController
@RequiredArgsConstructor
public class LoginController implements LoginControllerDocs {

    private final TokenCookieManager tokenCookieManager;
    private final LoginService loginService;

    @Override
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        String token = loginService.loginAndReturnToken(request);
        tokenCookieManager.addTokenCookie(response, token);
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping("/login/check")
    public ResponseEntity<LoginCheckResponse> loginCheck(@Login LoginMember loginMember) {
        String memberName = loginService.findMemberName(loginMember.id());
        LoginCheckResponse loginCheckResponse = new LoginCheckResponse(memberName);
        return ResponseEntity.ok().body(loginCheckResponse);
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        tokenCookieManager.deleteTokenCookie(response);
        return ResponseEntity.ok().build();
    }
}

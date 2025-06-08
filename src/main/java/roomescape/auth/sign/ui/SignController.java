package roomescape.auth.sign.ui;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.session.Session;
import roomescape.auth.session.annotation.UserSession;
import roomescape.auth.sign.application.SignFacade;
import roomescape.auth.sign.ui.dto.SignInWebRequest;
import roomescape.auth.sign.ui.dto.SignUpWebRequest;
import roomescape.auth.sign.ui.dto.UserSessionResponse;
import roomescape.common.uri.UriFactory;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class SignController {

    private final SignFacade signFacade;

    @PostMapping("/sign-in")
    @Operation(summary = "로그인")
    public ResponseEntity<Void> signIn(@RequestBody final SignInWebRequest signInWebRequest,
                                       final HttpServletResponse response) {
        signFacade.signIn(signInWebRequest, response);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sign-in/check")
    @Operation(summary = "로그인 상태 확인")
    public ResponseEntity<UserSessionResponse> checkSignIn(@UserSession final Session session) {
        return ResponseEntity.ok(
                UserSessionResponse.from(session));
    }

    @PostMapping("/sign-up")
    @Operation(summary = "회원 가입")
    public ResponseEntity<UserSessionResponse> create(@RequestBody final SignUpWebRequest request) {
        final UserSessionResponse response = signFacade.signUp(request);

        final URI location = UriFactory.buildPath("/users", String.valueOf(response.userId()));
        return ResponseEntity.created(location)
                .body(response);
    }
}

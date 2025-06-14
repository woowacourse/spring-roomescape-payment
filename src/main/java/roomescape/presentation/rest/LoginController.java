package roomescape.presentation.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.AuthenticationService;
import roomescape.application.UserService;
import roomescape.domain.auth.AuthenticationInfo;
import roomescape.presentation.auth.AuthenticationTokenCookie;
import roomescape.presentation.request.LoginRequest;
import roomescape.presentation.response.UserResponse;

@Tag(name = "Login", description = "로그인/로그아웃 관련 API")
@RestController
@AllArgsConstructor
public class LoginController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Operation(summary = "로그인", description = "이메일과 비밀번호를 입력하면 로그인을 요청합니다.")
    @PostMapping("/login")
    public void performLogin(
            @RequestBody @Valid final LoginRequest request,
            final HttpServletResponse response
    ) {
        var issuedToken = authenticationService.issueToken(request.email(), request.password());
        var tokenCookie = AuthenticationTokenCookie.forResponse(issuedToken);
        response.addCookie(tokenCookie);
    }

    @Operation(summary = "로그인 상태 확인", description = "현재 로그인 상태인지 확인합니다.")
    @GetMapping("/login/check")
    public UserResponse getUser(final AuthenticationInfo authenticationInfo) {
        var user = userService.getById(authenticationInfo.id());
        return UserResponse.from(user);
    }

    @Operation(summary = "로그아웃", description = "로그아웃을 요청합니다.")
    @PostMapping("/logout")
    public void performLogout(final HttpServletResponse response) throws IOException {
        var tokenCookieForExpire = AuthenticationTokenCookie.forExpire();
        response.addCookie(tokenCookieForExpire);
        response.sendRedirect("/");
    }
}

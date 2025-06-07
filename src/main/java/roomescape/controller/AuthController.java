package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.auth.AuthService;
import roomescape.domain.auth.dto.LoginMember;
import roomescape.domain.auth.dto.LoginRequest;
import roomescape.domain.auth.dto.LoginResponse;

@Tag(
        name = "인증 및 인가와 관련된 컨트롤러",
        description = "사용자 인증 및 인가 API, TOKEN 이름의 쿠키에 JWT를 담아 인증 및 인가 처리를 하고 있다."
)
@RestController
@AllArgsConstructor
public class AuthController {

    private static final String TOKEN_NAME = "token";

    private final AuthService authService;

    @Operation(
            description = "이메일과 비밀번호를 통해 로그인(인증)처리, TOKEN 이름의 쿠키에 JWT를 담아준다."
    )
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody @Valid final LoginRequest request
    ) {
        final String jwt = authService.generateToken(request);
        final ResponseCookie cookie = ResponseCookie
                .from(TOKEN_NAME, jwt)
                .secure(true)
                .httpOnly(true)
                .sameSite(SameSite.LAX.attributeValue())
                .build();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @Operation(
            description = "TOKEN 이름의 JWT를 통해 정보를 가져와 응답"
    )
    @GetMapping("/login/check")
    public ResponseEntity<LoginResponse> check(
            @AuthenticationPrincipal final LoginMember loginMember
    ) {
        final LoginResponse response = new LoginResponse(loginMember.name());
        return ResponseEntity.ok(response);
    }
}

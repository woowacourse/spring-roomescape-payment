package roomescape.auth.ui;

import static roomescape.auth.domain.AuthRole.ADMIN;
import static roomescape.auth.domain.AuthRole.MEMBER;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.application.AuthService;
import roomescape.auth.domain.MemberAuthInfo;
import roomescape.auth.domain.RequiresRole;
import roomescape.auth.ui.dto.LoginRequest;
import roomescape.member.ui.dto.MemberResponse;

@RestController
@RequiredArgsConstructor
@Tag(name = "로그인/로그아웃", description = "로그인/로그아웃 관련 API")
public class AuthRestController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "로그인")
    public ResponseEntity<Void> createAccessToken(
            @RequestBody @Valid final LoginRequest request
    ) {
        final String authToken = authService.createAccessToken(request);

        final ResponseCookie cookie = ResponseCookie.from("token", authToken)
                .path("/")
                .httpOnly(true)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @GetMapping("/login/check")
    @Operation(summary = "로그인 상태면 해당 회원의 이름을 응답")
    public ResponseEntity<MemberResponse.Name> checkAccessToken(
            final MemberAuthInfo memberAuthInfo
    ) {
        final MemberResponse.Name response = new MemberResponse.Name(
                authService.getMemberNameById(memberAuthInfo.id()));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @RequiresRole(authRoles = {ADMIN, MEMBER})
    @Operation(summary = "로그아웃")
    public ResponseEntity<Void> logout() {
        final ResponseCookie cookie = ResponseCookie.from("token", "")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}

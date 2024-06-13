package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.Login;
import roomescape.controller.dto.TokenRequest;
import roomescape.service.AuthService;
import roomescape.service.MemberService;
import roomescape.service.dto.request.LoginMember;
import roomescape.service.dto.response.MemberResponse;

@Tag(name = "[MEMBER] 로그인 API", description = "로그인/로그아웃 및 로그인한 사용자인지 확인할 수 있습니다.")
@RestController
public class LoginController {

    private final MemberService memberService;
    private final AuthService authService;

    public LoginController(MemberService memberService, AuthService authService) {
        this.memberService = memberService;
        this.authService = authService;
    }

    @Operation(summary = "로그인 API")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "헤더에 토큰정보를 담아 반환합니다.")
    })
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody TokenRequest tokenRequest) {
        MemberResponse memberResponse = memberService.findByEmailAndPassword(tokenRequest.email(), tokenRequest.password());
        String accessToken = authService.createToken(memberResponse);

        ResponseCookie cookie = ResponseCookie
                .from(authService.getTokenName(), accessToken)
                .httpOnly(true)
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    @Operation(summary = "로그인 체크 API", description = "로그인된 사용자 정보를 반환합니다.")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "인증/인가에 필요한 정보를 반환합니다.")
    })
    @GetMapping("/login/check")
    public ResponseEntity<MemberResponse> findMyInfo(@Login LoginMember loginMember) {
        MemberResponse memberResponse = loginMember.toMemberResponse();

        return ResponseEntity.ok()
                .body(memberResponse);
    }

    @Operation(summary = "로그아웃 API")
    @PostMapping("/logout")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "인증정보를 초기화합니다. 값이 null인 쿠키를 반환합니다.")
    })
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie
                .from(authService.getTokenName(), null)
                .httpOnly(true)
                .path("/")
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }
}

package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.request.LoginMemberRequest;
import roomescape.dto.request.LoginRequest;
import roomescape.entity.Member;
import roomescape.service.AuthService;
import roomescape.service.MemberService;
import roomescape.util.CookieUtil;

@Tag(name = "인증", description = "로그인/로그아웃 관련 API")
@RestController
public class AuthController {

    private final MemberService memberService;
    private final AuthService authService;

    public AuthController(MemberService memberService, AuthService authService) {
        this.memberService = memberService;
        this.authService = authService;
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "500", description = "내부 서버 에러")
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Parameter(description = "로그인 요청") @RequestBody @Valid LoginRequest request,
            @Parameter(description = "HTTP 응답 객체") HttpServletResponse response) {
        Member member = memberService.findByEmailAndPassword(request);
        String accessToken = authService.createTokenByMember(member);

        CookieUtil.addCookie("token", accessToken, response);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인 상태 확인", description = "현재 로그인된 사용자의 정보를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 상태 확인 성공")
    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    @ApiResponse(responseCode = "500", description = "내부 서버 에러")
    @GetMapping("/login/check")
    public ResponseEntity<LoginMemberRequest> checkLogin(
            @Parameter(description = "로그인한 사용자 정보") LoginMemberRequest loginMemberRequest) {
        return ResponseEntity.ok(loginMemberRequest);
    }

    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자를 로그아웃합니다.")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @ApiResponse(responseCode = "500", description = "내부 서버 에러")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(description = "HTTP 응답 객체") HttpServletResponse response) {
        CookieUtil.expireCookie("token", response);

        return ResponseEntity.ok().build();
    }
}

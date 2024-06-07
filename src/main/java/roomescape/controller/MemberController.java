package roomescape.controller;

import static roomescape.exception.ExceptionType.INVALID_TOKEN;
import static roomescape.exception.ExceptionType.LOGIN_FAIL;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.annotation.Auth;
import roomescape.annotation.ErrorApiResponse;
import roomescape.dto.LoginRequest;
import roomescape.dto.MemberInfo;
import roomescape.service.MemberService;
import roomescape.service.TokenService;

@RestController
@Tag(name = "회원", description = "회원 관리 API")
public class MemberController {
    private final MemberService memberService;
    private final TokenService tokenService;

    public MemberController(MemberService memberService, TokenService tokenService) {
        this.memberService = memberService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인 할 때 사용하는 API")
    @ErrorApiResponse(LOGIN_FAIL)
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {
        long memberId = memberService.login(loginRequest);
        LocalDateTime now = LocalDateTime.now();
        Duration tokenLifeTime = Duration.between(now, now.plusHours(1));
        String token = tokenService.createToken(memberId, now, tokenLifeTime);
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .maxAge(tokenLifeTime)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @GetMapping("/login/check")
    @Operation(summary = "로그인 여부 확인", description = "로그인 여부를 확인할 때 사용하는 API")
    @ErrorApiResponse(INVALID_TOKEN)
    public MemberInfo myInfo(@Auth long memberId) {
        return memberService.findByMemberId(memberId);
    }

    @GetMapping("/members")
    @Operation(summary = "회원 목록 조회", description = "회원 목록을 조회할 때 사용하는 API")
    public List<MemberInfo> allMembers() {
        return memberService.findAll();
    }
}

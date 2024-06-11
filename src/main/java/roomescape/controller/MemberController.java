package roomescape.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.annotation.Auth;
import roomescape.dto.LoginRequest;
import roomescape.dto.MemberInfo;
import roomescape.service.MemberService;
import roomescape.service.TokenService;

@RestController
@RequestMapping(produces = "application/json")
@Tag(name = "회원 API", description = "회원 관련 API 입니다.")
public class MemberController {

    private final MemberService memberService;
    private final TokenService tokenService;

    public MemberController(MemberService memberService, TokenService tokenService) {
        this.memberService = memberService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 API", description = "로그인 API 입니다.")
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
    @Operation(summary = "로그인 확인 API", description = "로그인 상태를 확인합니다.")
    public ResponseEntity<MemberInfo> myInfo(@Auth long memberId) {
        MemberInfo memberInfo = memberService.findByMemberId(memberId);
        return ResponseEntity.ok(memberInfo);
    }

    @GetMapping("/members")
    @Operation(summary = "회원 목록 조회 API", description = "모든 회원 목록을 조회합니다.")
    public List<MemberInfo> allMembers() {
        return memberService.findAll();
    }
}

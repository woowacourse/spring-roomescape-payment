package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.annotation.AuthenticationPrincipal;
import roomescape.controller.request.MemberLoginRequest;
import roomescape.controller.request.RegisterRequest;
import roomescape.controller.response.MemberNameResponse;
import roomescape.controller.response.MemberResponse;
import roomescape.model.Member;
import roomescape.service.AuthService;
import roomescape.service.MemberReadService;
import roomescape.service.MemberWriteService;

import java.net.URI;
import java.util.List;

@Tag(name = "member", description = "유저 계정 API")
@RestController
public class MemberController {

    private final MemberReadService memberReadService;
    private final MemberWriteService memberWriteService;
    private final AuthService authService;

    public MemberController(MemberReadService memberReadService, MemberWriteService memberWriteService, AuthService authService) {
        this.memberReadService = memberReadService;
        this.memberWriteService = memberWriteService;
        this.authService = authService;
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody MemberLoginRequest request, HttpServletResponse response) {
        Member member = memberReadService.getMemberByEmailAndPassword(request);
        Cookie cookie = authService.createCookieByMember(member);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인 확인", description = "로그인 정보를 통해 이름을 가져옵니다.")
    @GetMapping("/login/check")
    public ResponseEntity<MemberNameResponse> login(@AuthenticationPrincipal Member member) {
        return ResponseEntity.ok(new MemberNameResponse(member.getName()));
    }

    @Operation(summary = "멤버 조회", description = "모든 멤버를 조회합니다.")
    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        List<Member> members = memberReadService.findAllMembers();
        List<MemberResponse> responses = members.stream()
                .map(MemberResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "로그아웃", description = "로그아웃 합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        response.addCookie(authService.expireCookie(request.getCookies()));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "멤버 추가", description = "기입 정보에 따라 멤버를 등록합니다.")
    @PostMapping("/members")
    public ResponseEntity<MemberResponse> registerMember(@RequestBody RegisterRequest registerRequest) {
        Member member = memberWriteService.register(registerRequest);
        MemberResponse response = new MemberResponse(member);
        return ResponseEntity.created(URI.create("/members/" + member.getId())).body(response);
    }
}

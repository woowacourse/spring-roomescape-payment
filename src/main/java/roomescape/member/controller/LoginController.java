package roomescape.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.dto.SessionMember;
import roomescape.member.controller.dto.LoginCheckResponse;
import roomescape.member.controller.dto.LoginRequest;
import roomescape.member.domain.Member;

@RestController
@RequestMapping("/login")
public class LoginController {
    private final MemberService memberService;

    public LoginController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "로그인 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 사용자입니다.",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "비밀번호가 일치하지 않습니다.",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @PostMapping
    public ResponseEntity<Void> login(@RequestBody final LoginRequest request, final HttpSession session) {
        Member member = memberService.login(request);
        session.setAttribute("LOGIN_MEMBER", new SessionMember(member.getId(), member.getName(), member.getRole()));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인 정보 조회 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 정보 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인이 필요합니다.",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @GetMapping("/check")
    public ResponseEntity<LoginCheckResponse> loginCheck(final HttpSession httpSession) {
        SessionMember sessionMember = (SessionMember) httpSession.getAttribute("LOGIN_MEMBER");
        LoginCheckResponse loginCheckResponse = new LoginCheckResponse(sessionMember.name().name());
        return ResponseEntity.ok(loginCheckResponse);
    }
}

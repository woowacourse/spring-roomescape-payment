package roomescape.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.global.dto.ErrorResponse;
import roomescape.global.dto.SessionMember;
import roomescape.member.controller.dto.LoginCheckResponse;
import roomescape.member.controller.dto.LoginRequest;
import roomescape.member.domain.Member;

@Tag(name = "로그인 API")
@RestController
@RequestMapping("/login")
public class LoginController {
    private final MemberService memberService;

    public LoginController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인에 성공한다."),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않아 로그인에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 정보가 올바르지 않아 로그인에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류로 인해 로그인에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Void> login(@RequestBody final LoginRequest request, final HttpSession session) {
        Member member = memberService.login(request);
        session.setAttribute("LOGIN_MEMBER", new SessionMember(member.getId(), member.getName(), member.getRole()));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인된 사용자의 권한 정보를 반환한다.",
                    content = @Content(schema = @Schema(implementation = LoginCheckResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인하지 않아 사용자 정보를 확인할 수 없다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류로 인해 로그인 상태 확인에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/check")
    public ResponseEntity<LoginCheckResponse> loginCheck(final HttpSession httpSession) {
        SessionMember sessionMember = (SessionMember) httpSession.getAttribute("LOGIN_MEMBER");
        LoginCheckResponse loginCheckResponse = new LoginCheckResponse(sessionMember.name().name());
        return ResponseEntity.ok(loginCheckResponse);
    }
}

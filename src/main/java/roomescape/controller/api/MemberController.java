package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.auth.SignUpRequest;
import roomescape.dto.member.MemberResponse;
import roomescape.dto.member.MemberSignupResponse;
import roomescape.service.MemberService;

@Tag(name = "유저 API", description = "회원 조회 및 회원가입 관련 API입니다.")
@RestController
public class MemberController {

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "전체 회원 조회", description = "가입된 모든 회원 정보를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "회원 목록 반환 성공")
    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> getMembers() {
        List<MemberResponse> allMembers = memberService.findAllMembers();
        return ResponseEntity.ok(allMembers);
    }

    @Operation(summary = "회원가입", description = "새 사용자를 등록합니다.")
    @ApiResponse(responseCode = "200", description = "회원가입 성공")
    @PostMapping("/members")
    public ResponseEntity<MemberSignupResponse> signup(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원가입 요청 정보",
                    required = true
            )
            @RequestBody SignUpRequest request) {
        MemberSignupResponse response = memberService.registerMember(request);
        log.info("User sign up: email={}, name={}", request.email(), request.name());
        return ResponseEntity.ok().body(response);
    }
}

package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.auth.SignUpRequest;
import roomescape.dto.member.MemberResponse;
import roomescape.dto.member.MemberSignupResponse;
import roomescape.service.MemberService;

@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "사용자 전체 조회 API")
    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> getMembers() {
        List<MemberResponse> allMembers = memberService.findAllMembers();
        return ResponseEntity.ok(allMembers);
    }

    @Operation(summary = "회원가입 API")
    @PostMapping("/members")
    public ResponseEntity<MemberSignupResponse> signup(@RequestBody SignUpRequest request) {
        MemberSignupResponse response = memberService.registerMember(request);
        return ResponseEntity.ok().body(response);
    }
}

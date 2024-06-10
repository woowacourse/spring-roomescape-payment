package roomescape.web.controller.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.member.MemberResponse;
import roomescape.service.member.MemberService;

@Tag(name = "회원 관리")
@RestController
class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "모든 회원 조회", description = "등록된 모든 회원을 조회한다.")
    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        List<MemberResponse> memberResponses = memberService.findAllMembers();
        return ResponseEntity.ok(memberResponses);
    }
}

package roomescape.controller.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.config.auth.RoleAllowed;
import roomescape.domain.member.MemberRole;
import roomescape.service.member.MemberService;
import roomescape.service.member.dto.MemberListResponse;

@Tag(name = "Member")
@RestController
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @RoleAllowed(MemberRole.ADMIN)
    @GetMapping("/members")
    @Operation(summary = "[관리자] 전체 회원 정보 조회", description = "전체 회원 정보를 조회한다.")
    public ResponseEntity<MemberListResponse> findAllMember() {
        MemberListResponse response = memberService.findAllMember();
        return ResponseEntity.ok().body(response);
    }
}

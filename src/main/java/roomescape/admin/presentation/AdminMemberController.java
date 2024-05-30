package roomescape.admin.presentation;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.AdminOnly;
import roomescape.member.dto.MemberResponse;
import roomescape.member.service.MemberService;

@RestController
public class AdminMemberController {

    private final MemberService memberService;

    public AdminMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/admin/members")
    @AdminOnly
    public ResponseEntity<List<MemberResponse>> readAll() {
        return ResponseEntity.ok(memberService.findAll());
    }
}

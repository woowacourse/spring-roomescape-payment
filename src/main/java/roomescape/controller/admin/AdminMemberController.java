package roomescape.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.service.MemberService;
import roomescape.service.dto.response.MemberResponses;

@RestController
public class AdminMemberController {

    private final MemberService memberService;

    public AdminMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "사용자조회 API", description = "사용자를 조회한다.")
    @GetMapping("/admin/members")
    public ResponseEntity<MemberResponses> findAll() {
        MemberResponses memberResponses = memberService.findAll();

        return ResponseEntity.ok()
                .body(memberResponses);
    }

    @Operation(summary = "사용자 삭제 API", description = "사용자를 삭제한다.")
    @DeleteMapping("/admin/members/{id}")
    public ResponseEntity<Void> withdraw(@PathVariable("id") Long id) {
        memberService.withdraw(id);
        return ResponseEntity.noContent().build();
    }
}

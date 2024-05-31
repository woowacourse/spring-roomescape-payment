package roomescape.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.service.MemberService;
import roomescape.service.dto.MemberResponse;

import java.util.List;

@RestController
@RequestMapping("/admin/members")
public class AdminMemberController {

    private final MemberService memberService;

    public AdminMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<List<MemberResponse>> findAll() {
        List<MemberResponse> memberResponses = memberService.findAll();

        return ResponseEntity.ok()
                .body(memberResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> withdraw(@PathVariable("id") Long id) {
        memberService.withdraw(id);
        return ResponseEntity.noContent().build();
    }
}

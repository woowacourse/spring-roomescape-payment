package roomescape.web.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.MemberService;
import roomescape.application.dto.response.member.MemberResponse;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/admin/members")
    public ResponseEntity<List<MemberResponse>> findAllMembers() {
        List<MemberResponse> response = memberService.findAllMember();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/members/{idMember}")
    public ResponseEntity<Void> withdrawal(@PathVariable("idMember") Long memberId) {
        memberService.withdrawal(memberId);
        return ResponseEntity.noContent().build();
    }
}

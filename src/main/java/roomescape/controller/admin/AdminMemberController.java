package roomescape.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.response.MemberResponse;
import roomescape.service.member.MemberService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class AdminMemberController {

    private final MemberService memberService;

    @GetMapping("/admin/members")
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        List<MemberResponse> response = memberService.getAllMembers();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

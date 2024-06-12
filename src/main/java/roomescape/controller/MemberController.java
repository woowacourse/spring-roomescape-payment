package roomescape.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.response.MemberResponse;
import roomescape.service.MemberService;

import java.util.List;

@Tag(name = "Member", description = "회원 API")
@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> findAllMembers() {
        List<MemberResponse> loginMemberResponses = memberService.findAll();
        return ResponseEntity.ok(loginMemberResponses);
    }
}

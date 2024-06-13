package roomescape.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import roomescape.controller.api.docs.MemberApiDocs;
import roomescape.controller.dto.response.MemberResponse;
import roomescape.service.MemberService;

@RestController
@RequestMapping("/members")
public class MemberController implements MemberApiDocs {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<List<MemberResponse>> findAll() {
        List<MemberResponse> response = memberService.findAll();
        return ResponseEntity.ok(response);
    }
}

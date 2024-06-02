package roomescape.controller.api;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.dto.request.SignupRequest;
import roomescape.controller.dto.response.ApiResponses;
import roomescape.service.MemberService;
import roomescape.service.dto.response.MemberResponse;

@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/members")
    public ResponseEntity<MemberResponse> signup(@RequestBody @Valid SignupRequest request) {
        MemberResponse memberResponse = memberService.createMember(request.toCreateMemberRequest());
        return ResponseEntity.created(URI.create("/members/" + memberResponse.id()))
                .body(memberResponse);
    }

    @GetMapping("/admin/members")
    public ApiResponses<MemberResponse> getAllMembers() {
        List<MemberResponse> memberResponses = memberService.getAllMembers();
        return new ApiResponses<>(memberResponses);
    }
}

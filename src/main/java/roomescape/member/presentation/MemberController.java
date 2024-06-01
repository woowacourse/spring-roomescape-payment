package roomescape.member.presentation;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.dto.JoinRequest;
import roomescape.member.dto.MemberResponse;
import roomescape.member.service.MemberService;

@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/members")
    public ResponseEntity<MemberResponse> createMember(@RequestBody @Valid JoinRequest joinRequest) {
        MemberResponse createdResponse = memberService.joinMember(joinRequest);
        URI createdUri = URI.create("/members/" + createdResponse.id());
        return ResponseEntity.created(createdUri).body(createdResponse);
    }
}

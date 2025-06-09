package roomescape.member.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.member.dto.request.SignupRequest;
import roomescape.member.dto.response.MemberResponse;
import roomescape.member.dto.response.SignupResponse;
import roomescape.member.service.MemberService;

import java.net.URI;
import java.util.List;

import static roomescape.member.presentation.MemberController.RESERVATION_BASE_URL;

@RestController
@RequestMapping(RESERVATION_BASE_URL)
public class MemberController {

    public static final String RESERVATION_BASE_URL = "/members";
    private static final String SLASH = "/";

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest request) {
        SignupResponse response = memberService.createUser(request);
        URI uri = URI.create(RESERVATION_BASE_URL + SLASH + response.id());
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MemberResponse>> findAllMembers() {
        List<MemberResponse> allMember = memberService.findAllMember();
        return ResponseEntity.ok().body(allMember);
    }
}

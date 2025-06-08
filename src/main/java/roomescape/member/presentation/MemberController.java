package roomescape.member.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.member.adaptor.MemberApiAdaptor;
import roomescape.member.docs.MemberResponseDocs;
import roomescape.member.docs.SignupRequestDocs;
import roomescape.member.docs.SignupResponseDocs;
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

    private final MemberApiAdaptor memberApiAdaptor;
    private final MemberService memberService;

    public MemberController(final MemberService memberService, final MemberApiAdaptor memberApiAdaptor) {
        this.memberService = memberService;
        this.memberApiAdaptor = memberApiAdaptor;
    }

    @PostMapping
    public ResponseEntity<SignupResponseDocs> signup(@RequestBody SignupRequestDocs requestDocs) {
        SignupRequest request = memberApiAdaptor.toSignupRequest(requestDocs);

        SignupResponse response = memberService.createUser(request);
        SignupResponseDocs signupResponseDocs = memberApiAdaptor.toSignupResponseDocs(response);

        URI uri = URI.create(RESERVATION_BASE_URL + SLASH + response.id());
        return ResponseEntity.created(uri).body(signupResponseDocs);
    }

    @GetMapping
    public ResponseEntity<List<MemberResponseDocs>> findAllMembers() {
        List<MemberResponse> allMember = memberService.findAllMember();

        List<MemberResponseDocs> responseDocs = allMember.stream().map(memberApiAdaptor::toMemberResponseDocs).toList();

        return ResponseEntity.ok().body(responseDocs);
    }
}

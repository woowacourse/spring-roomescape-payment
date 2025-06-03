package roomescape.presentation.api.member;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.member.command.CreateMemberService;
import roomescape.application.member.query.MemberQueryService;
import roomescape.application.member.query.dto.MemberResult;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private static final String MEMBERS_URL = "/members/%d";

    private final CreateMemberService createMemberService;
    private final MemberQueryService memberQueryService;

    public MemberController(final CreateMemberService createMemberService, final MemberQueryService memberQueryService) {
        this.createMemberService = createMemberService;
        this.memberQueryService = memberQueryService;
    }

    @PostMapping
    public ResponseEntity<Void> createMember(@Valid @RequestBody final SignupRequest signupRequest) {
        final Long id = createMemberService.register(signupRequest.toRegisterCommand());
        return ResponseEntity.created(URI.create(MEMBERS_URL.formatted(id))).build();
    }

    @GetMapping
    public ResponseEntity<List<MemberResponse>> findAll() {
        final List<MemberResult> memberResults = memberQueryService.findAll();
        final List<MemberResponse> memberResponses = memberResults.stream()
                .map(MemberResponse::from)
                .toList();
        return ResponseEntity.ok(memberResponses);
    }
}

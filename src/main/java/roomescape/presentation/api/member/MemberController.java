package roomescape.presentation.api.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "회원 API")
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

    @Operation(
            summary = "회원 가입",
            description = "새로운 회원을 등록합니다. 요청 본문에 필요한 회원 정보를 포함해야 합니다."
    )
    @PostMapping
    public ResponseEntity<Void> createMember(@Valid @RequestBody final SignupRequest signupRequest) {
        final Long id = createMemberService.register(signupRequest.toRegisterCommand());
        return ResponseEntity.created(URI.create(MEMBERS_URL.formatted(id))).build();
    }

    @Operation(
            summary = "회원 조회",
            description = "모든 회원을 조회합니다. 각 회원의 상세 정보가 포함됩니다."
    )
    @GetMapping
    public ResponseEntity<List<MemberResponse>> findAll() {
        final List<MemberResult> memberResults = memberQueryService.findAll();
        final List<MemberResponse> memberResponses = memberResults.stream()
                .map(MemberResponse::from)
                .toList();
        return ResponseEntity.ok(memberResponses);
    }
}

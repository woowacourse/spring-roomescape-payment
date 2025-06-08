package roomescape.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.request.MemberRequest;
import roomescape.dto.response.MemberResponse;
import roomescape.service.MemberService;

@Tag(name = "멤버 API", description = "멤버 API 입니다.")
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "모든 회원 조회", description = "모든 회원을 조회합니다.")
    @GetMapping
    public List<MemberResponse> getMembers() {
        return memberService.findAllMembers();
    }

    @Operation(summary = "회원 가입", description = "회원 가입을 수행합니다.")
    @PostMapping
    public ResponseEntity<MemberResponse> signUp(@RequestBody @Valid MemberRequest memberRequest) {
        return ResponseEntity.ok(memberService.createMember(memberRequest));
    }
}

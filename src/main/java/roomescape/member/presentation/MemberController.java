package roomescape.member.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.domain.Member;
import roomescape.member.dto.JoinRequest;
import roomescape.member.dto.MemberResponse;
import roomescape.member.service.MemberService;

@RestController
@Tag(name = "Member API", description = "사용자 회원 가입 관련 API")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "전체 사용자 조회 API", description = "전체 사용자를 조회합니다.")
    @GetMapping("/members")
    public ResponseEntity<List<Member>> readAll() {
        return ResponseEntity.ok(memberService.findAll());
    }

    @Operation(summary = "사용자 회원가입 API", description = "사용자 회원 가입 시 요청합니다.")
    @PostMapping("/members")
    public ResponseEntity<MemberResponse> createMember(@RequestBody @Valid JoinRequest joinRequest) {
        MemberResponse createdResponse = memberService.joinMember(joinRequest);
        URI createdUri = URI.create("/members/" + createdResponse.id());
        return ResponseEntity.created(createdUri).body(createdResponse);
    }
}

package roomescape.member.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.application.MemberService;
import roomescape.member.application.dto.MemberResponse;

@Tag(name = "회원", description = "회원 관련 API")
@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "모든 회원 조회 API", description = "등록된 모든 회원의 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<MemberResponse>> findAll() {
        return ResponseEntity.ok(memberService.findAll());
    }
}

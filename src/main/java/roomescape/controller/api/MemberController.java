package roomescape.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.controller.dto.response.MemberResponse;
import roomescape.service.MemberService;

@Tag(name = "Member", description = "사용자 관련 API")
@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "모든 사용자 조회", description = "모든 사용자 정보를 조회할 수 있다.")
    @GetMapping
    public ResponseEntity<List<MemberResponse>> findAll() {
        List<MemberResponse> response = memberService.findAll();
        return ResponseEntity.ok(response);
    }
}

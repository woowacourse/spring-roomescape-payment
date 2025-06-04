package roomescape.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.response.MemberResponse;
import roomescape.service.member.MemberService;

import java.util.List;

@Tag(name = "5. 어드민 전용 API")
@RequiredArgsConstructor
@RestController
public class AdminMemberController {

    private final MemberService memberService;

    @Operation(summary = "모든 멤버 조회")
    @GetMapping("/admin/members")
    public ResponseEntity<List<MemberResponse>> getAll() {
        List<MemberResponse> response = memberService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

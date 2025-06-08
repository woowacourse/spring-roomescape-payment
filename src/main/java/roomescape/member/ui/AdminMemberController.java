package roomescape.member.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.response.ApiResponse;
import roomescape.member.application.MemberService;
import roomescape.member.application.dto.MemberResponse;

@Tag(name = "멤버 API For 관리자", description = "관리자만 호출 가능한 멤버 관련 API입니다.")
@RestController
@RequestMapping("admin/members")
@AllArgsConstructor
public class AdminMemberController {
    private final MemberService memberService;

    @Operation(summary = "멤버 조회", description = "모든 멤버를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getAll() {
        ApiResponse<List<MemberResponse>> apiResponse = ApiResponse.createSuccess(memberService.findAll());
        return ResponseEntity.ok(apiResponse);
    }
}

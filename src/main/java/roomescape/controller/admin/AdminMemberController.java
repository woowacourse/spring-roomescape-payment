package roomescape.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.service.MemberService;
import roomescape.service.dto.response.MemberResponses;

@Tag(name = "[ADMIN] 사용자 API", description = "어드민 권한으로 사용자를 조회/삭제할 수 있습니다.")
@RestController
public class AdminMemberController {

    private final MemberService memberService;

    public AdminMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "사용자조회 API")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "전체 회원 정보를 반환합니다.")
    })
    @GetMapping("/admin/members")
    public ResponseEntity<MemberResponses> findAll() {
        MemberResponses memberResponses = memberService.findAll();

        return ResponseEntity.ok()
                .body(memberResponses);
    }

    @Operation(summary = "사용자 삭제 API")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "204", description = "사용자 삭제에 성공했습니다.")
    })
    @DeleteMapping("/admin/members/{id}")
    public ResponseEntity<Void> withdraw(@PathVariable("id") Long id) {
        memberService.withdraw(id);
        return ResponseEntity.noContent().build();
    }
}

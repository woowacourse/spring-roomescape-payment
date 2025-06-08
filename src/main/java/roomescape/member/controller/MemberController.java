package roomescape.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.dto.ErrorResponse;
import roomescape.member.controller.dto.MemberResponse;

import java.util.List;

@Tag(name = "멤버 API")
@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "멤버 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 멤버 정보를 반환한다."),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류로 인해 멤버 정보 조회에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<MemberResponse>> findMembers() {
        return ResponseEntity.ok(memberService.findAllMembers());
    }
}

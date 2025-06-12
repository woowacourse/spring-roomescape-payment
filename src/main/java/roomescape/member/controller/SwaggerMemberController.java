package roomescape.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import roomescape.common.exception.error.ErrorResponse;
import roomescape.member.controller.dto.MemberInfoResponse;

@Tag(name = "회원 관리", description = "회원 API")
public interface SwaggerMemberController {

    @Operation(summary = "전체 회원 정보 조회", description = "모든 회원의 정보를 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "조회", content = @Content(schema = @Schema(implementation = MemberInfoResponse.class)))
    })
    List<MemberInfoResponse> getMembers();
}

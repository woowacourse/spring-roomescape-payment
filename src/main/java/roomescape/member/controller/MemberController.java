package roomescape.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import roomescape.member.controller.dto.MemberInfoResponse;

@Tag(name = "멤버 API [관리자 권한]")
public interface MemberController {

    @Operation(summary = "멤버 목록 조회 API", security = @SecurityRequirement(name = "loginAuth"))
    List<MemberInfoResponse> getMembers();
}

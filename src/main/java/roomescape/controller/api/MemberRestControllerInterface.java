package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import roomescape.domain.member.dto.MemberResponse;

@Tag(name = "Member", description = "멤버 관련 API")
@RequestMapping("/members")
public interface MemberRestControllerInterface {

    @Operation(summary = "멤버 조회", description = "멤버를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "멤버 조회 성공")
    })
    @GetMapping
    ResponseEntity<List<MemberResponse>> getMembers();
}

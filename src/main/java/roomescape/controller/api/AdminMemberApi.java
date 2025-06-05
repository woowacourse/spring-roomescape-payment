package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import roomescape.dto.response.MemberResponse;

import java.util.List;

@Tag(name = "1. 멤버 관련 API")
public interface AdminMemberApi {

    @Operation(summary = "모든 멤버 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
    })
    ResponseEntity<List<MemberResponse>> getAll();
}

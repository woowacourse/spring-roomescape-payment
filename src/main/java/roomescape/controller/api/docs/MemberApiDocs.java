package roomescape.controller.api.docs;

import java.util.List;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.controller.dto.response.ErrorMessageResponse;
import roomescape.controller.dto.response.MemberResponse;

@Tag(name = "Member", description = "사용자 관련 API")
public interface MemberApiDocs {
    @Operation(summary = "모든 사용자 조회", description = "모든 사용자 정보를 조회할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = {@Content(schema = @Schema(implementation = MemberResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    ResponseEntity<List<MemberResponse>> findAll();
}

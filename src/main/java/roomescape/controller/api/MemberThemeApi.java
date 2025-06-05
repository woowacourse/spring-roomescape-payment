package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import roomescape.dto.response.ReservationThemeResponse;

import java.util.List;

@Tag(name = "3. 테마 관련 API")
public interface MemberThemeApi {

    @Operation(summary = "모든 테마 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
    })
    ResponseEntity<List<ReservationThemeResponse>> getAll();

    @Operation(summary = "인기 테마 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
    })
    ResponseEntity<List<ReservationThemeResponse>> getPopulars();
}

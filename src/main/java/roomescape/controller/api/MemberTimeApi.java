package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import roomescape.dto.response.ReservationTimeWithAvailabilityResponse;
import roomescape.global.exception.ErrorResponse;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "4. 예약 시간 관련 API")
public interface MemberTimeApi {

    @Operation(summary = "테마, 날짜의 예약 가능 시간 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "404", description = "id에 해당하는 테마가 없는 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    ResponseEntity<List<ReservationTimeWithAvailabilityResponse>> getAvailables(
            @Parameter(example = "1", required = true) long themeId,
            @Parameter(example = "2025-06-05", required = true) LocalDate date
    );
}

package roomescape.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.application.dto.response.time.AvailableReservationTimeResponse;
import roomescape.application.dto.response.time.ReservationTimeResponse;

@Tag(name = "예약 시간 조회", description = "예약 시간 조회 API")
interface MemberReservationTimeControllerDocs {

    @Operation(summary = "예약 시간 전체 조회", description = "예약 시간 전체를 조회한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "예약 시간 전체 조회 성공"
            )
    })
    ResponseEntity<List<ReservationTimeResponse>> findAllTimes();

    @Operation(summary = "예약 가능 시간 조회", description = "예약 가능 시간을 조회한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "예약 가능 시간 조회 성공"
            )
    })
    ResponseEntity<List<AvailableReservationTimeResponse>> findAllAvailableTimes(
            @Parameter(description = "시간", example = "2024-10-01") LocalDate date,
            @Parameter(description = "테마 ID", example = "1") Long themeId
    );
}

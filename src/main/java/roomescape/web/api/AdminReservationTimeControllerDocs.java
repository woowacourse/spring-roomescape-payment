package roomescape.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import roomescape.application.dto.request.time.ReservationTimeRequest;
import roomescape.application.dto.response.time.ReservationTimeResponse;

@Tag(name = "관리자 예약 시간 관리", description = "관리자 예약 시간 관리 API")
interface AdminReservationTimeControllerDocs {

    @Operation(summary = "예약 시간 등록", description = "예약 시간을 등록한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "예약 시간 등록 성공"
            )
    })
    ResponseEntity<ReservationTimeResponse> saveReservationTime(
            @Valid ReservationTimeRequest request
    );

    @Operation(summary = "예약 시간 삭제", description = "예약 시간을 삭제한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "예약 시간 삭제 성공"
            )
    })
    ResponseEntity<Void> deleteReservationTime(
            @Parameter(description = "예약 시간 ID", example = "1") Long timeId
    );
}

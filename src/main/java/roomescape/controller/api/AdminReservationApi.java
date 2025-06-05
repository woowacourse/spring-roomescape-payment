package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import roomescape.dto.request.CreateReservationRequest;
import roomescape.dto.response.PendingReservationResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.global.exception.ErrorResponse;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "2. 예약 관련 API")
public interface AdminReservationApi {

    @Operation(summary = "예약 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "400", description = "이미 예약/대기 내역이 존재하는 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    ResponseEntity<ReservationResponse> save(
            @RequestBody(required = true) CreateReservationRequest request
    );

    @Operation(summary = "예약 필터링 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
    })
    ResponseEntity<List<ReservationResponse>> getAllByFilter(
            @Parameter(example = "1") Long memberId,
            @Parameter(example = "1") Long themeId,
            @Parameter(example = "2025-06-03") LocalDate dateFrom,
            @Parameter(example = "2025-06-07") LocalDate dateTo
    );

    @Operation(summary = "대기 예약 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
    })
    ResponseEntity<List<PendingReservationResponse>> getAllPendings();

    @Operation(summary = "예약 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "404", description = "id에 해당하는 예약이 없는 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    ResponseEntity<Void> remove(
            @Parameter(example = "1", required = true) long reservationId
    );

    @Operation(summary = "대기 예약 거절")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "400", description = "대기가 아닌 예약인 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "id에 해당하는 예약이 없는 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    ResponseEntity<Void> denyPending(
            @Parameter(example = "1", required = true) long reservationId
    );
}

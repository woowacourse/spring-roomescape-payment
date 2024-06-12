package roomescape.controller.reservation;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import roomescape.domain.reservation.ReservationTime;
import roomescape.dto.ErrorResponse;
import roomescape.dto.reservation.AvailableReservationTimeResponse;
import roomescape.dto.reservation.AvailableReservationTimeSearch;
import roomescape.dto.reservation.ReservationTimeResponse;
import roomescape.dto.reservation.ReservationTimeSaveRequest;
import roomescape.service.ReservationTimeService;

@RestController
@RequestMapping("/times")
public class ReservationTimeController {
    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(final ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "예약 시간 생성")
    @ApiResponse(responseCode = "201", description = "예약 시간 생성 성공")
    @PostMapping
    public ResponseEntity<ReservationTimeResponse> createReservationTime(
            @RequestBody final ReservationTimeSaveRequest request
    ) {
        final ReservationTime reservationTime = request.toModel();
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationTimeService.create(reservationTime));
    }

    @Operation(summary = "예약 시간 조회")
    @ApiResponse(responseCode = "200", description = "예약 시간 조회 성공")
    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> findReservationTimes() {
        return ResponseEntity.ok(reservationTimeService.findAll());
    }

    @Operation(summary = "예약 시간 삭제")
    @ApiResponse(responseCode = "204", description = "예약 시간 삭제 성공")
    @ApiResponse(
            responseCode = "404",
            description = "예약 시간 삭제 실패 - 존재하지 않는 id",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationTime(@PathVariable final Long id) {
        reservationTimeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "예약되지 않은 시간 조회")
    @ApiResponse(responseCode = "200", description = "예약되지 않은 시간 조회 성공")
    @GetMapping("/available")
    public ResponseEntity<List<AvailableReservationTimeResponse>> findAvailableReservationTimes(
            @ModelAttribute final AvailableReservationTimeSearch availableReservationTimeSearch
    ) {
        return ResponseEntity.ok(reservationTimeService.findAvailableReservationTimes(availableReservationTimeSearch));
    }
}

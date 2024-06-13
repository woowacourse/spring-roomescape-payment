package roomescape.controller.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.domain.reservation.ReservationTime;
import roomescape.dto.reservation.AvailableReservationTimeResponse;
import roomescape.dto.reservation.AvailableReservationTimeSearch;
import roomescape.service.ReservationTimeService;
import roomescape.dto.reservation.ReservationTimeResponse;
import roomescape.dto.reservation.ReservationTimeSaveRequest;

import java.util.List;

@Tag(name = "예약시각 API")
@RestController
@RequestMapping("/times")
public class ReservationTimeController {
    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(final ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "에약시각 생성")
    @PostMapping
    public ResponseEntity<ReservationTimeResponse> createReservationTime(@RequestBody final ReservationTimeSaveRequest request) {
        final ReservationTime reservationTime = request.toModel();
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationTimeService.create(reservationTime));
    }

    @Operation(summary = "예약시각 목록")
    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> findReservationTimes() {
        return ResponseEntity.ok(reservationTimeService.findAll());
    }

    @Operation(summary = "예약시각 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationTime(@PathVariable final Long id) {
        reservationTimeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "예약 가능 시각 목록",description = "주어진 필터 정보로 현재 예약 가능 시각을 불러옵니다.")
    @GetMapping("/available")
    public ResponseEntity<List<AvailableReservationTimeResponse>> findAvailableReservationTimes(
            @ModelAttribute final AvailableReservationTimeSearch availableReservationTimeSearch) {
        return ResponseEntity.ok(reservationTimeService.findAvailableReservationTimes(availableReservationTimeSearch));
    }
}

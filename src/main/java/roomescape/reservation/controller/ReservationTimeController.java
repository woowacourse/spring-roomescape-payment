package roomescape.reservation.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.reservation.dto.ReservationTimeResponse;
import roomescape.reservation.service.ReservationTimeService;

@RestController
@Tag(name = "예약 시간", description = "예약 시간 관련 API")
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(final ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "예약 시간 조회", description = "모든 예약 시간을 조회하는 API")
    @GetMapping("/times")
    public List<ReservationTimeResponse> getReservationTimes() {
        return reservationTimeService.getReservationTimes()
                .stream()
                .map(ReservationTimeResponse::from)
                .toList();
    }
}

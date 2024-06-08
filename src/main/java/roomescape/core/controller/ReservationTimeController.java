package roomescape.core.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.core.dto.reservationtime.BookedTimeResponse;
import roomescape.core.dto.reservationtime.ReservationTimeResponse;
import roomescape.core.service.ReservationTimeService;

@Tag(name = "예약 시간 관리 API")
@RestController
@RequestMapping("/times")
public class ReservationTimeController {
    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(final ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "모든 예약 시간 조회")
    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> findAll() {
        return ResponseEntity.ok(reservationTimeService.findAll());
    }

    @Operation(summary = "특정 날짜, 테마에 대해 이미 예약이 존재하는지 여부와 함께 모든 예약 시간 조회")
    @GetMapping(params = {"date", "theme"})
    public ResponseEntity<List<BookedTimeResponse>> findAllWithBookable(@RequestParam("date") String date,
                                                                        @RequestParam("theme") Long themeId) {
        return ResponseEntity.ok(reservationTimeService.findAllWithBookable(date, themeId));
    }
}

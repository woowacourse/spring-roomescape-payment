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

@Tag(name = "예약 시간 API", description = "예약 시간 관련 API 입니다.")
@RestController
@RequestMapping("/times")
public class ReservationTimeController {
    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(final ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "예약 시간 조회 API")
    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> findAll() {
        return ResponseEntity.ok(reservationTimeService.findAll());
    }

    @Operation(summary = "예약 가능한 시간 조회 API")
    @GetMapping(params = {"date", "theme"})
    public ResponseEntity<List<BookedTimeResponse>> findAllWithBookable(@RequestParam("date") String date,
                                                                        @RequestParam("theme") Long themeId) {
        return ResponseEntity.ok(reservationTimeService.findAllWithBookable(date, themeId));
    }
}

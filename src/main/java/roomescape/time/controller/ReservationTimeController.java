package roomescape.time.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import roomescape.time.dto.ReservationTimeCreateRequest;
import roomescape.time.dto.ReservationTimeResponse;
import roomescape.time.service.ReservationTimeService;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "예약 시간 컨트롤러")
@RestController
@RequestMapping("/times")
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "예약 시간 목록 조회")
    @GetMapping
    public List<ReservationTimeResponse> readTimes() {
        return reservationTimeService.readReservationTimes();
    }

    @Operation(summary = "예약 가능 시간 조회")
    @GetMapping(params = {"date", "themeId"})
    public List<ReservationTimeResponse> readTimes(
            @RequestParam(value = "date") LocalDate date,
            @RequestParam(value = "themeId") Long themeId
    ) {
        return reservationTimeService.readReservationTimes(date, themeId);
    }

    @Operation(summary = "예약 조회")
    @GetMapping("/{id}")
    public ReservationTimeResponse readTime(@PathVariable Long id) {
        return reservationTimeService.readReservationTime(id);

    }

    @Operation(summary = "예약 시간 생성")
    @PostMapping
    public ReservationTimeResponse createTime(@Valid @RequestBody ReservationTimeCreateRequest request) {
        return reservationTimeService.createTime(request);
    }

    @Operation(summary = "예약 시간 삭제")
    @DeleteMapping("/{id}")
    public void deleteTime(@PathVariable Long id) {
        reservationTimeService.deleteTime(id);
    }
}

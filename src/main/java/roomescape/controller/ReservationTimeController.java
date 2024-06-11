package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.controller.request.ReservationTimeRequest;
import roomescape.controller.response.IsReservedTimeResponse;
import roomescape.controller.response.ReservationTimeResponse;
import roomescape.model.ReservationTime;
import roomescape.service.ReservationTimeReadService;
import roomescape.service.ReservationTimeWriteService;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "reservation-time", description = "방탈출 시간 API")
@RestController
public class ReservationTimeController {

    private final ReservationTimeReadService reservationTimeReadService;
    private final ReservationTimeWriteService reservationTimeWriteService;

    public ReservationTimeController(ReservationTimeReadService reservationTimeReadService, ReservationTimeWriteService reservationTimeWriteService) {
        this.reservationTimeReadService = reservationTimeReadService;
        this.reservationTimeWriteService = reservationTimeWriteService;
    }

    @Operation(summary = "방탈출 시간 조회", description = "모든 방탈출 시간을 조회합니다.")
    @GetMapping("/times")
    public ResponseEntity<List<ReservationTimeResponse>> getReservationTimes() {
        List<ReservationTime> reservationTimes = reservationTimeReadService.findAllReservationTimes();
        List<ReservationTimeResponse> responses = reservationTimes.stream()
                .map(time -> new ReservationTimeResponse(time.getId(), time.getStartAt()))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "방탈출 시간 등록", description = "방탈출 시간을 등록합니다.")
    @PostMapping("/times")
    public ResponseEntity<ReservationTimeResponse> createReservationTime(@RequestBody ReservationTimeRequest request) {
        ReservationTime reservationTime = reservationTimeWriteService.addReservationTime(request);
        ReservationTimeResponse response = new ReservationTimeResponse(reservationTime.getId(), reservationTime.getStartAt());
        return ResponseEntity.created(URI.create("/times/" + reservationTime.getId())).body(response);
    }

    @Operation(summary = "방탈출 시간 삭제", description = "방탈출 시간을 삭제합니다.")
    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> deleteReservationTime(@PathVariable("id") long id) {
        reservationTimeWriteService.deleteReservationTime(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "예약된 시간인지 확인", description = "해당 시간이 예약되었는지 여부를 확인합니다.")
    @GetMapping("/times/reserved")
    public ResponseEntity<List<IsReservedTimeResponse>> getPossibleReservationTimes(
            @RequestParam(name = "date") LocalDate date,
            @RequestParam(name = "themeId") long themeId) {
        List<IsReservedTimeResponse> response = reservationTimeReadService.getIsReservedTime(date, themeId);
        return ResponseEntity.ok(response);
    }
}

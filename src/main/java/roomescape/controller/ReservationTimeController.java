package roomescape.controller;

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

@RestController
public class ReservationTimeController {

    private final ReservationTimeReadService reservationTimeReadService;
    private final ReservationTimeWriteService reservationTimeWriteService;

    public ReservationTimeController(ReservationTimeReadService reservationTimeReadService, ReservationTimeWriteService reservationTimeWriteService) {
        this.reservationTimeReadService = reservationTimeReadService;
        this.reservationTimeWriteService = reservationTimeWriteService;
    }

    @GetMapping("/times")
    public ResponseEntity<List<ReservationTimeResponse>> getReservationTimes() {
        List<ReservationTime> reservationTimes = reservationTimeReadService.findAllReservationTimes();
        List<ReservationTimeResponse> responses = reservationTimes.stream()
                .map(time -> new ReservationTimeResponse(time.getId(), time.getStartAt()))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/times")
    public ResponseEntity<ReservationTimeResponse> createReservationTime(@RequestBody ReservationTimeRequest request) {
        ReservationTime reservationTime = reservationTimeWriteService.addReservationTime(request);
        ReservationTimeResponse response = new ReservationTimeResponse(reservationTime.getId(), reservationTime.getStartAt());
        return ResponseEntity.created(URI.create("/times/" + reservationTime.getId())).body(response);
    }

    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> deleteReservationTime(@PathVariable("id") long id) {
        reservationTimeWriteService.deleteReservationTime(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/times/reserved")
    public ResponseEntity<List<IsReservedTimeResponse>> getPossibleReservationTimes(
            @RequestParam(name = "date") LocalDate date,
            @RequestParam(name = "themeId") long themeId) {
        List<IsReservedTimeResponse> response = reservationTimeReadService.getIsReservedTime(date, themeId);
        return ResponseEntity.ok(response);
    }
}
